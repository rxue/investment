import { useEffect, useState, useRef } from 'react'
import { createChart, LineSeries } from 'lightweight-charts'

const chartSize = { width: 1200, height: 500 };
const colors = { benchmark: "red", compared: "blue" };


function generateChart(container, benchmarkData, colors) {
    const chart = createChart(container, chartSize);
    const benchmarkReturns = benchmarkData.benchmark;
    const benchmarkSeries = chart.addSeries(LineSeries, { color: colors.benchmark });
    benchmarkSeries.setData(benchmarkReturns.valueSeries);
    const comparedSeries = chart.addSeries(LineSeries);
    comparedSeries.setData(benchmarkData.compared[0].valueSeries);
    chart.timeScale().fitContent();
    return chart;
}


function SolidLine({ color }) {
    return <span style={{ color }}>────── </span>;
}
function BenchmarkChart({ benchmarkReturnsData }) {
    const containerRef = useRef(null);
    useEffect(() => {
        if (!benchmarkReturnsData) return;
        const chart = generateChart(containerRef.current, benchmarkReturnsData, colors);
        return () => chart.remove();
    }, [benchmarkReturnsData]);

    return (<>
                <div ref={containerRef} />
                <p><SolidLine color={ colors.benchmark } />Benchmark returns - {benchmarkReturnsData.benchmark.id}</p>
                <p><SolidLine color={ colors.compared } />Compared returns - {benchmarkReturnsData.compared[0].id}</p>
            </>
            );
}
export default function Benchmark() {
    const [returnsData, setReturnsData] = useState(null);

    // 1. fetch data -> state
    useEffect(() => {
        const controller = new AbortController();
        fetch("/api/benchmark?benchmarkSecurityId=VOO&securityId=PFE&inclusiveStartDate=2023-01-01&inclusiveEndDate=2026-11-22", { signal: controller.signal })
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
    return returnsData && <BenchmarkChart benchmarkReturnsData={returnsData} />;
}
