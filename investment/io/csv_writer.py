import pandas as pd


def write(input: list[dict[str, str]], output_file_path: str) -> None:
    pd.DataFrame(input).to_csv(output_file_path, index=False)
