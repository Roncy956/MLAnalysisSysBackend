import sys
import json
import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.linear_model import Ridge
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import mean_squared_error, r2_score, mean_absolute_error
import xgboost as xgb
import lightgbm as lgb
from catboost import CatBoostRegressor

from RandomForest import preprocess_data


def create_models():
    return {
        'RandomForest': RandomForestRegressor(
            n_estimators=100, max_depth=20, min_samples_split=5,
            min_samples_leaf=2, random_state=42, n_jobs=-1
        ),
        'XGBoost': xgb.XGBRegressor(
            objective='reg:tweedie', tweedie_variance_power=1.2,
            n_estimators=100, max_depth=8, learning_rate=0.1,
            subsample=0.8, colsample_bytree=0.8,
            random_state=42, n_jobs=-1
        ),
        'LightGBM': lgb.LGBMRegressor(
            objective='tweedie', tweedie_variance_power=1.2,
            n_estimators=100, max_depth=10, learning_rate=0.1,
            num_leaves=31, subsample=0.8, colsample_bytree=0.8,
            reg_alpha=0.0, reg_lambda=0.0,
            random_state=42, n_jobs=-1, verbose=-1
        ),
        'CatBoost': CatBoostRegressor(
            iterations=200, depth=6, learning_rate=0.1,
            subsample=1.0, random_seed=42, verbose=False
        ),
    }


def train_ensemble(X, y):
    # 三路划分: 70% 基模型训练 / 15% 元模型训练 / 15% 最终测试
    X_train, X_temp, y_train, y_temp = train_test_split(
        X, y, test_size=0.3, random_state=42
    )
    X_meta, X_test, y_meta, y_test = train_test_split(
        X_temp, y_temp, test_size=0.5, random_state=42
    )

    models = create_models()
    y_train_log = np.log1p(y_train)

    individual_preds = {}
    individual_metrics = {}

    for name, model in models.items():
        if name == 'CatBoost':
            model.fit(X_train, y_train_log)
            pred_test = np.expm1(model.predict(X_test))
            pred_meta = np.expm1(model.predict(X_meta))
        else:
            model.fit(X_train, y_train)
            pred_test = model.predict(X_test)
            pred_meta = model.predict(X_meta)

        individual_preds[name] = {'meta': pred_meta, 'test': pred_test}

        individual_metrics[name] = {
            'test_r2': float(r2_score(y_test, pred_test)),
            'test_rmse': float(np.sqrt(mean_squared_error(y_test, pred_test))),
            'test_mae': float(mean_absolute_error(y_test, pred_test)),
        }

    # Simple Average
    avg_pred = np.mean([individual_preds[n]['test'] for n in models], axis=0)
    simple_avg_metrics = {
        'test_r2': float(r2_score(y_test, avg_pred)),
        'test_rmse': float(np.sqrt(mean_squared_error(y_test, avg_pred))),
        'test_mae': float(mean_absolute_error(y_test, avg_pred)),
    }

    # Weighted Average (按 test R² 加权，负值置零)
    r2_scores = np.array([max(0, individual_metrics[n]['test_r2']) for n in models])
    w = r2_scores / r2_scores.sum()
    wgt_pred = np.average([individual_preds[n]['test'] for n in models], axis=0, weights=w)
    weighted_avg_metrics = {
        'weights': {name: float(w_i) for name, w_i in zip(models.keys(), w)},
        'test_r2': float(r2_score(y_test, wgt_pred)),
        'test_rmse': float(np.sqrt(mean_squared_error(y_test, wgt_pred))),
        'test_mae': float(mean_absolute_error(y_test, wgt_pred)),
    }

    # Stacking (Ridge 元模型)
    meta_features = np.column_stack([individual_preds[n]['meta'] for n in models])
    meta_model = Ridge(alpha=1.0)
    meta_model.fit(meta_features, y_meta)

    test_meta_features = np.column_stack([individual_preds[n]['test'] for n in models])
    stack_pred = meta_model.predict(test_meta_features)
    stacking_metrics = {
        'weights': {name: float(c) for name, c in zip(models.keys(), meta_model.coef_)},
        'intercept': float(meta_model.intercept_),
        'test_r2': float(r2_score(y_test, stack_pred)),
        'test_rmse': float(np.sqrt(mean_squared_error(y_test, stack_pred))),
        'test_mae': float(mean_absolute_error(y_test, stack_pred)),
    }

    # 综合特征重要性（四个模型归一化后取平均）
    all_importances = {}
    for name, model in models.items():
        raw = model.feature_importances_
        normalized = raw / raw.sum()
        all_importances[name] = normalized

    avg_importance = np.mean(list(all_importances.values()), axis=0)
    importance_list = [
        {'feature': feat, 'importance': float(imp)}
        for feat, imp in zip(X.columns, avg_importance)
    ]
    importance_list.sort(key=lambda x: x['importance'], reverse=True)

    # Plot data（以 stacking 预测为最终输出）
    plot_data = {
        'actual': y_test.tolist(),
        'predicted': stack_pred.tolist(),
        'residuals': (y_test - stack_pred).tolist(),
        'ensemble_method': 'stacking',
        'individual_preds': {
            name: individual_preds[name]['test'].tolist()
            for name in models
        },
    }

    return {
        'individual_metrics': individual_metrics,
        'simple_average': simple_avg_metrics,
        'weighted_average': weighted_avg_metrics,
        'stacking': stacking_metrics,
        'feature_importance': importance_list[:20],
        'plot_data': plot_data,
    }


def main():
    if len(sys.argv) < 2 or sys.argv[1] != 'train':
        print(json.dumps({"status": "error", "message": "Usage: python Ensemble.py train"}))
        sys.exit(1)

    try:
        json_str = sys.stdin.read()
        if not json_str:
            raise ValueError("No input JSON provided")
        input_data = json.loads(json_str)
        data_list = input_data.get('data', [])
        if not data_list:
            raise ValueError("Input JSON missing 'data' key or empty array")
        df = pd.DataFrame(data_list)
    except Exception as e:
        print(json.dumps({"status": "error", "message": f"Failed to parse input JSON: {str(e)}"}))
        sys.exit(1)

    try:
        X, y, _ = preprocess_data(df)
        result = train_ensemble(X, y)
        result['status'] = 'success'
        print(json.dumps(result, ensure_ascii=False))
    except Exception as e:
        print(json.dumps({"status": "error", "message": f"Ensemble training failed: {str(e)}"}))
        sys.exit(1)


if __name__ == "__main__":
    main()
