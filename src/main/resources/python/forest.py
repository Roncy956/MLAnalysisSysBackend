# marine_economy_predictor.py
import sys
import json
import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestRegressor
from sklearn.preprocessing import LabelEncoder
from sklearn.metrics import mean_squared_error, r2_score, mean_absolute_error
import warnings
warnings.filterwarnings('ignore')

class MarineEconomyPredictor:
    def __init__(self):
        self.model = None
        self.encoders = {}
        self.feature_columns = [
            'location', 'product_encoded', 'year', 'diversity', 'ubiquity',
            'mcp', 'eci', 'pci', 'density', 'coi', 'cog', 'rca',
            'product_name_encoded', 'type_encoded', 'color_encoded',
            'total_value_by_year', 'total_value_by_year_location',
            'total_value_by_year_product', 'total_value_by_year_type',
            'total_value_by_year_location_type'
        ]

    def preprocess_data(self, df):
        """预处理数据"""
        df_processed = df.copy()

        # 编码分类变量
        self.encoders['le_product'] = LabelEncoder()
        self.encoders['le_product_name'] = LabelEncoder()
        self.encoders['le_type'] = LabelEncoder()
        self.encoders['le_color'] = LabelEncoder()

        df_processed['product_encoded'] = self.encoders['le_product'].fit_transform(df_processed['product'].astype(str))
        df_processed['product_name_encoded'] = self.encoders['le_product_name'].fit_transform(df_processed['product_name'])
        df_processed['type_encoded'] = self.encoders['le_type'].fit_transform(df_processed['type'])

        if df_processed['color'].isna().any():
            df_processed['color'] = df_processed['color'].fillna('Unknown')
        df_processed['color_encoded'] = self.encoders['le_color'].fit_transform(df_processed['color'])

        X = df_processed[self.feature_columns]
        y = df_processed['value']

        return X, y

    def train_model(self, df):
        """训练模型"""
        X, y = self.preprocess_data(df)

        X_train, X_test, y_train, y_test = train_test_split(
            X, y, test_size=0.2, random_state=42
        )

        self.model = RandomForestRegressor(
            n_estimators=100,
            max_depth=20,
            min_samples_split=5,
            min_samples_leaf=2,
            random_state=42,
            n_jobs=-1
        )

        self.model.fit(X_train, y_train)

        # 预测
        y_train_pred = self.model.predict(X_train)
        y_test_pred = self.model.predict(X_test)

        # 计算评估指标
        metrics = {
            'train_r2': float(r2_score(y_train, y_train_pred)),
            'test_r2': float(r2_score(y_test, y_test_pred)),
            'train_mse': float(mean_squared_error(y_train, y_train_pred)),
            'test_mse': float(mean_squared_error(y_test, y_test_pred)),
            'test_mae': float(mean_absolute_error(y_test, y_test_pred)),
            'test_rmse': float(np.sqrt(mean_squared_error(y_test, y_test_pred)))
        }

        # 特征重要性
        importance_df = pd.DataFrame({
            'feature': self.feature_columns,
            'importance': self.model.feature_importances_
        }).sort_values('importance', ascending=False)

        feature_importance = []
        for _, row in importance_df.iterrows():
            feature_importance.append({
                'feature': row['feature'],
                'importance': float(row['importance'])
            })

        # 准备绘图数据
        plot_data = {
            'actual_vs_predicted': {
                'actual': y_test.tolist(),
                'predicted': y_test_pred.tolist()
            },
            'residuals': {
                'predicted': y_test_pred.tolist(),
                'residuals': (y_test - y_test_pred).tolist()
            }
        }

        return {
            'metrics': metrics,
            'feature_importance': feature_importance,
            'plot_data': plot_data,
            'model_info': {
                'training_samples': len(X_train),
                'testing_samples': len(X_test),
                'total_samples': len(df)
            }
        }

    def predict_single(self, input_data):
        """单次预测"""
        if self.model is None:
            raise ValueError("模型尚未训练")

        # 创建输入DataFrame
        input_df = pd.DataFrame([input_data])

        # 编码
        input_df['product_encoded'] = self.encoders['le_product'].transform(input_df['product'].astype(str))
        input_df['product_name_encoded'] = self.encoders['le_product_name'].transform(input_df['product_name'])
        input_df['type_encoded'] = self.encoders['le_type'].transform(input_df['type'])

        if pd.isna(input_df['color'].iloc[0]):
            input_df['color'] = 'Unknown'
        input_df['color_encoded'] = self.encoders['le_color'].transform(input_df['color'])

        X_input = input_df[self.feature_columns]
        prediction = self.model.predict(X_input)

        return float(prediction[0])

def main():
    if len(sys.argv) < 2:
        print(json.dumps({"error": "缺少参数"}))
        sys.exit(1)

    command = sys.argv[1]

    if command == "train":
        # 从stdin读取JSON数据
        input_json = sys.stdin.read()
        data = json.loads(input_json)

        # 转换为DataFrame
        df = pd.DataFrame(data['data'])

        predictor = MarineEconomyPredictor()
        result = predictor.train_model(df)

        # 添加模型保存逻辑
        import pickle
        with open('trained_model.pkl', 'wb') as f:
            pickle.dump(predictor, f)

        print(json.dumps(result, ensure_ascii=False))

    elif command == "predict":
        # 从stdin读取JSON数据
        input_json = sys.stdin.read()
        data = json.loads(input_json)

        # 加载已训练的模型
        import pickle
        with open('trained_model.pkl', 'rb') as f:
            predictor = pickle.load(f)

        result = predictor.predict_single(data['input'])
        print(json.dumps({"prediction": result}))

    elif command == "load_and_predict":
        # 同时训练和预测
        input_json = sys.stdin.read()
        data = json.loads(input_json)

        df = pd.DataFrame(data['data'])
        input_sample = data['input']

        predictor = MarineEconomyPredictor()
        train_result = predictor.train_model(df)

        prediction = predictor.predict_single(input_sample)
        train_result['single_prediction'] = prediction

        # 保存模型
        import pickle
        with open('trained_model.pkl', 'wb') as f:
            pickle.dump(predictor, f)

        print(json.dumps(train_result, ensure_ascii=False))

if __name__ == "__main__":
    main()