INSERT INTO tblProduct9 (productId,productName,brand,model,origin,unit,listPrice,stockQuantity,warrantyPeriod,warrantyRequired,description,status,categoryId)
SELECT 'MH' || LPAD(CAST(NEXT VALUE FOR productSeq AS VARCHAR),5,'0'),
'Smart Tivi LG UHD 4K 50 inch','LG','50UT8050','Việt Nam','chiếc',8900000,16,24,TRUE,'Smart Tivi LG UHD 4K 50 inch','Đang kinh doanh','NH001'
WHERE NOT EXISTS (SELECT 1 FROM tblProduct9 WHERE productName='Smart Tivi LG UHD 4K 50 inch' AND model='50UT8050');

INSERT INTO tblProduct9 (productId,productName,brand,model,origin,unit,listPrice,stockQuantity,warrantyPeriod,warrantyRequired,description,status,categoryId)
SELECT 'MH' || LPAD(CAST(NEXT VALUE FOR productSeq AS VARCHAR),5,'0'),
'Tủ lạnh Samsung Inverter 280 lít','Samsung','RB27N4010','Việt Nam','chiếc',7900000,18,24,TRUE,'Tủ lạnh Samsung Inverter 280 lít','Đang kinh doanh','NH002'
WHERE NOT EXISTS (SELECT 1 FROM tblProduct9 WHERE productName='Tủ lạnh Samsung Inverter 280 lít' AND model='RB27N4010');

INSERT INTO tblProduct9 (productId,productName,brand,model,origin,unit,listPrice,stockQuantity,warrantyPeriod,warrantyRequired,description,status,categoryId)
SELECT 'MH' || LPAD(CAST(NEXT VALUE FOR productSeq AS VARCHAR),5,'0'),
'Máy giặt Electrolux Inverter 9kg','Electrolux','EWF9024','Việt Nam','chiếc',10500000,12,24,TRUE,'Máy giặt Electrolux Inverter 9kg','Đang kinh doanh','NH003'
WHERE NOT EXISTS (SELECT 1 FROM tblProduct9 WHERE productName='Máy giặt Electrolux Inverter 9kg' AND model='EWF9024');

INSERT INTO tblProduct9 (productId,productName,brand,model,origin,unit,listPrice,stockQuantity,warrantyPeriod,warrantyRequired,description,status,categoryId)
SELECT 'MH' || LPAD(CAST(NEXT VALUE FOR productSeq AS VARCHAR),5,'0'),
'Điều hòa Panasonic Inverter 9000 BTU','Panasonic','CU-CS-PU9','Việt Nam','chiếc',9800000,15,24,TRUE,'Điều hòa Panasonic Inverter 9000 BTU','Đang kinh doanh','NH004'
WHERE NOT EXISTS (SELECT 1 FROM tblProduct9 WHERE productName='Điều hòa Panasonic Inverter 9000 BTU' AND model='CU-CS-PU9');

INSERT INTO tblProduct9 (productId,productName,brand,model,origin,unit,listPrice,stockQuantity,warrantyPeriod,warrantyRequired,description,status,categoryId)
SELECT 'MH' || LPAD(CAST(NEXT VALUE FOR productSeq AS VARCHAR),5,'0'),
'Nồi cơm điện Toshiba 1.8 lít','Toshiba','RC-18JH2','Việt Nam','chiếc',1450000,25,24,TRUE,'Nồi cơm điện Toshiba 1.8 lít','Đang kinh doanh','NH005'
WHERE NOT EXISTS (SELECT 1 FROM tblProduct9 WHERE productName='Nồi cơm điện Toshiba 1.8 lít' AND model='RC-18JH2');

INSERT INTO tblProduct9 (productId,productName,brand,model,origin,unit,listPrice,stockQuantity,warrantyPeriod,warrantyRequired,description,status,categoryId)
SELECT 'MH' || LPAD(CAST(NEXT VALUE FOR productSeq AS VARCHAR),5,'0'),
'Quạt điện Panasonic F-409K','Panasonic','F-409K','Việt Nam','chiếc',2100000,22,24,TRUE,'Quạt điện Panasonic F-409K','Đang kinh doanh','NH006'
WHERE NOT EXISTS (SELECT 1 FROM tblProduct9 WHERE productName='Quạt điện Panasonic F-409K' AND model='F-409K');

INSERT INTO tblProduct9 (productId,productName,brand,model,origin,unit,listPrice,stockQuantity,warrantyPeriod,warrantyRequired,description,status,categoryId)
SELECT 'MH' || LPAD(CAST(NEXT VALUE FOR productSeq AS VARCHAR),5,'0'),
'Lò vi sóng Sharp 20 lít','Sharp','R-205VN','Việt Nam','chiếc',1700000,14,24,TRUE,'Lò vi sóng Sharp 20 lít','Đang kinh doanh','NH007'
WHERE NOT EXISTS (SELECT 1 FROM tblProduct9 WHERE productName='Lò vi sóng Sharp 20 lít' AND model='R-205VN');

INSERT INTO tblProduct9 (productId,productName,brand,model,origin,unit,listPrice,stockQuantity,warrantyPeriod,warrantyRequired,description,status,categoryId)
SELECT 'MH' || LPAD(CAST(NEXT VALUE FOR productSeq AS VARCHAR),5,'0'),
'Máy hút bụi Samsung 2000W','Samsung','VC20M2510','Việt Nam','chiếc',2900000,17,24,TRUE,'Máy hút bụi Samsung 2000W','Đang kinh doanh','NH008'
WHERE NOT EXISTS (SELECT 1 FROM tblProduct9 WHERE productName='Máy hút bụi Samsung 2000W' AND model='VC20M2510');

INSERT INTO tblProduct9 (productId,productName,brand,model,origin,unit,listPrice,stockQuantity,warrantyPeriod,warrantyRequired,description,status,categoryId)
SELECT 'MH' || LPAD(CAST(NEXT VALUE FOR productSeq AS VARCHAR),5,'0'),
'Máy lọc không khí Sharp FP-J40E','Sharp','FP-J40E','Việt Nam','chiếc',3600000,13,24,TRUE,'Máy lọc không khí Sharp FP-J40E','Đang kinh doanh','NH009'
WHERE NOT EXISTS (SELECT 1 FROM tblProduct9 WHERE productName='Máy lọc không khí Sharp FP-J40E' AND model='FP-J40E');

INSERT INTO tblProduct9 (productId,productName,brand,model,origin,unit,listPrice,stockQuantity,warrantyPeriod,warrantyRequired,description,status,categoryId)
SELECT 'MH' || LPAD(CAST(NEXT VALUE FOR productSeq AS VARCHAR),5,'0'),
'Bình nóng lạnh Panasonic 30 lít','Panasonic','DH-30HAM','Việt Nam','chiếc',3900000,11,24,TRUE,'Bình nóng lạnh Panasonic 30 lít','Đang kinh doanh','NH010'
WHERE NOT EXISTS (SELECT 1 FROM tblProduct9 WHERE productName='Bình nóng lạnh Panasonic 30 lít' AND model='DH-30HAM');
