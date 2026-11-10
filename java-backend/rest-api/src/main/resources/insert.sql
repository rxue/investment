-- quote_metrics is currently mapped by Hibernate as an array of QuoteMetric ORDINALS (tinyint array):
-- 1 = LATEST_MARKET_PRICE, 3 = REGULAR_MARKET_CHANGE_PERCENT
INSERT INTO watch_list (id, name, security_ids, quote_metrics)
VALUES (NEXT VALUE FOR watch_list_seq, 'Default', ARRAY['ELISA.HE', 'NOVO-B.CO'], ARRAY[1, 3]);

