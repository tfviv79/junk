import pandas as pd

d = pd.read_stata('https://users.nber.org/~rdehejia/data/cps_controls.dta')
# d = pd.read_stata('./cps_controls.dta')

columns = [x for x in d.keys() if x != "data_id"]

# 対象のテーブル名
table_name = 'cps_controls'

print(f"INSERT INTO {table_name} ({', '.join(columns)}) VALUES")

sep = ""
for _, row in d.iterrows():
    vals = []
    for col in columns:
        val = row[col]
        if pd.isna(val):
            # 欠損値はNULLにする
            vals.append('NULL')
        elif col in ['black', 'hispanic', 'married', 'nodegree']:
            if val == 1.0:
                vals.append("TRUE")
            else:
                vals.append("FALSE")
        elif isinstance(val, str):
            # 文字列の場合はシングルクォーテーションで囲み、内部の「'」をエスケープ
            escaped_val = val.replace("'", "''")
            vals.append(f"'{escaped_val}'")
        else:
            # 数値などはそのまま文字列化
            vals.append(str(val))
    values = ', '.join(vals)
    print(f"  {sep}({values})")
    sep = ", "

print(";")
