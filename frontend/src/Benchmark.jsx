import { useEffect, useState, useRef } from 'react'
import { createChart, LineSeries } from 'lightweight-charts'

function generateChart(container, prices) {
    console.log("Prices: "+ prices);
    const chart = createChart(container, { width: 1000, height: 500 });
    const series = chart.addSeries(LineSeries);
    series.setData(prices);
    chart.timeScale().fitContent();
    return chart;
}

export default function Benchmark() {
    const [returnsData, setReturnsData] = useState(null);
    const containerRef = useRef(null);

    // 1. fetch data -> state
    useEffect(() => {
        const controller = new AbortController();
        fetch("/api/benchmark?securityId=PFE&benchmarkSecurityId=VOO&inclusiveStartDate=2025-01-01&inclusiveEndDate=2026-11-22", { signal: controller.signal })
            .then(response => {
                if (!response.ok) throw new Error(`HTTP ${response.status}`);
                return response.json();
            })
            .then(setReturnsData)
            .catch(error => {
                console.log("response error::::" + error);
                if (error.name !== 'AbortError') console.error(error);
            });
        return () => controller.abort();
    }, []);

    // 2. state -> chart
    useEffect(() => {
        if (!returnsData) return;
        const chart = generateChart(containerRef.current, returnsData);
        return () => chart.remove();
    }, [returnsData]);

    return <div ref={containerRef} />;
}
