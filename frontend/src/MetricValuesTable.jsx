

export default function MetricValuesTable(Props) {
    const metricData = Props.values;
    const metricNames = Object.keys(Object.values(metricData)[0]);

    return (
        <table>
            <thead>
                <tr>
                    <th>Symbol</th>
                    {metricNames.map((name) => (
                        <th key={name}>{name}</th>
                    ))}
                </tr>
            </thead>
            <tbody>
                {Object.entries(metricData).map(([symbol, metrics]) => (
                    <tr key={symbol}>
                        <td>{symbol}</td>
                        {metricNames.map((name) => (
                            <td key={name}>{metrics[name]}</td>
                        ))}
                    </tr>
                ))}
            </tbody>
        </table>
    );
}
