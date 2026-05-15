import sys
import json
import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder
from sklearn.metrics import mean_squared_error, r2_score, mean_absolute_error
import lightgbm as lgb


def preprocess_data(df):
    """
    预处理数据：编码分类变量，选择特征
    """
    df_processed = df.copy()

    le_product = LabelEncoder()
    le_product_name = LabelEncoder()
    le_type = LabelEncoder()
    le_color = LabelEncoder()

    df_processed['product_encoded'] = le_product.fit_transform(df_processed['product'].astype(str))
    df_processed['product_name_encoded'] = le_product_name.fit_transform(df_processed['product_name'])
    df_processed['type_encoded'] = le_type.fit_transform(df_processed['type'])


    feature_columns = [
        'location', 'product_encoded', 'year', 'diversity', 'ubiquity',
        'mcp', 'eci', 'pci', 'density', 'coi', 'cog', 'rca',
        'product_name_encoded', 'type_encoded',
        'total_value_by_year', 'total_value_by_year_location',
        'total_value_by_year_product', 'total_value_by_year_type',
        'total_value_by_year_location_type'
    ]

    X = df_processed[feature_columns]
    y = df_processed['value']

    return X, y, {
        'le_product': le_product,
        'le_product_name': le_product_name,
        'le_type': le_type,
        'le_color': le_color
    }


def train_lightgbm(X, y):
    """
    训练 LightGBM 模型，返回评估指标、特征重要性和绘图数据
    """
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.2, random_state=42
    )

    model = lgb.LGBMRegressor(
        objective='tweedie',          # 1. 将目标函数设为'tweedie'
        tweedie_variance_power=1.2,   # 2. 设置方差参数，对应XGBoost中的同名参数
        n_estimators=100,
        max_depth=10,
        learning_rate=0.1,
        num_leaves=31,
        subsample=0.8,
        colsample_bytree=0.8,
        reg_alpha=0.0,
        reg_lambda=0.0,
        random_state=42,
        n_jobs=-1,
        verbose=-1
    )

    model.fit(X_train, y_train)

    y_train_pred = model.predict(X_train)
    y_test_pred = model.predict(X_test)

    # 评估指标
    metrics = {
        'train_r2': float(r2_score(y_train, y_train_pred)),
        'test_r2': float(r2_score(y_test, y_test_pred)),
        'train_mse': float(mean_squared_error(y_train, y_train_pred)),
        'test_mse': float(mean_squared_error(y_test, y_test_pred)),
        'test_mae': float(mean_absolute_error(y_test, y_test_pred)),
        'test_rmse': float(np.sqrt(mean_squared_error(y_test, y_test_pred)))
    }

    # 特征重要性（使用 split 重要性）
    importance = model.feature_importances_
    feature_names = X.columns.tolist()
    importance_list = [
        {'feature': name, 'importance': float(imp)}
        for name, imp in zip(feature_names, importance)
    ]
    importance_list.sort(key=lambda x: x['importance'], reverse=True)

    # 绘图数据：actual（真实值）、predict（预测值）、residuals（残差）
    plot_data = {
        'actual': y_test.tolist(),
        'predicted': y_test_pred.tolist(),
        'residuals': (y_test - y_test_pred).tolist()
    }

    return metrics, importance_list, plot_data


def main():
    if len(sys.argv) < 2 or sys.argv[1] != 'train':
        print(json.dumps({"status": "error", "message": "Usage: python ds01.py train"}))
        sys.exit(1)

    # 从标准输入读取 JSON 数据
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

    # 训练并输出结果
    try:
        X, y, _ = preprocess_data(df)
        metrics, importance, plot_data = train_lightgbm(X, y)

        result = {
            "status": "success",
            "metrics": metrics,
            "feature_importance": importance[:20],   # 前20个重要特征
            "plot_data": plot_data
        }
        print(json.dumps(result, ensure_ascii=False))
    except Exception as e:
        print(json.dumps({"status": "error", "message": f"Training failed: {str(e)}"}))
        sys.exit(1)


if __name__ == "__main__":
    main()