args=()
for f in /home/rui/Documents/investment/company_data/tiliote/extracted/*.csv; do
  args+=(-F "file=@$f")
done
curl --include -X POST http://localhost:8080/irr "${args[@]}"
