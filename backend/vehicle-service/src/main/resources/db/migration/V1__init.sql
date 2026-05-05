CREATE TABLE IF NOT EXISTS vehicles (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    brand         VARCHAR(60) NOT NULL,
    type          VARCHAR(30) NOT NULL,
    transmission  VARCHAR(20) NOT NULL,
    fuel          VARCHAR(20) NOT NULL,
    seats         INT NOT NULL,
    price_per_day DECIMAL(10,2) NOT NULL,
    location      VARCHAR(100) NOT NULL,
    status        VARCHAR(20) NOT NULL,
    image_url     VARCHAR(500),
    description   VARCHAR(500),
    license_plate VARCHAR(30) UNIQUE,
    year_made     INT,
    rating        DECIMAL(3,1),
    created_at    DATETIME NOT NULL,
    updated_at    DATETIME,
    INDEX idx_type (type),
    INDEX idx_location (location),
    INDEX idx_status (status)
);

INSERT INTO vehicles (name, brand, type, transmission, fuel, seats, price_per_day, location, status, image_url, description, license_plate, year_made, rating, created_at, updated_at) VALUES
('Swift VXi', 'Maruti Suzuki', 'HATCHBACK', 'MANUAL', 'PETROL', 5, 1500.00, 'Mumbai', 'AVAILABLE', 'https://images.unsplash.com/photo-1494976388531-d1058494cdd8?w=800', 'Compact, efficient, perfect for city drives.', 'MH01AA1001', 2022, 4.5, NOW(), NOW()),
('Creta SX', 'Hyundai', 'SUV', 'AUTOMATIC', 'DIESEL', 5, 3200.00, 'Bangalore', 'AVAILABLE', 'https://images.unsplash.com/photo-1583121274602-3e2820c69888?w=800', 'Spacious SUV with premium interior.', 'KA01BB2002', 2023, 4.7, NOW(), NOW()),
('Camry Hybrid', 'Toyota', 'SEDAN', 'AUTOMATIC', 'HYBRID', 5, 5500.00, 'Delhi', 'AVAILABLE', 'https://images.unsplash.com/photo-1621007947382-bb3c3994e3fb?w=800', 'Premium hybrid sedan with elegant styling.', 'DL01CC3003', 2024, 4.8, NOW(), NOW()),
('Model 3', 'Tesla', 'LUXURY', 'AUTOMATIC', 'ELECTRIC', 5, 8500.00, 'Mumbai', 'AVAILABLE', 'https://images.unsplash.com/photo-1560958089-b8a1929cea89?w=800', 'Pure electric performance and luxury.', 'MH02DD4004', 2024, 4.9, NOW(), NOW()),
('Innova Crysta', 'Toyota', 'VAN', 'MANUAL', 'DIESEL', 7, 4200.00, 'Pune', 'AVAILABLE', 'https://images.unsplash.com/photo-1502877338535-766e1452684a?w=800', 'Family-friendly 7-seater MPV.', 'MH12EE5005', 2023, 4.6, NOW(), NOW()),
('Classic 350', 'Royal Enfield', 'BIKE', 'MANUAL', 'PETROL', 2, 800.00, 'Goa', 'AVAILABLE', 'https://images.unsplash.com/photo-1558981806-ec527fa84c39?w=800', 'Iconic cruiser for road trips.', 'GA01FF6006', 2023, 4.7, NOW(), NOW()),
('Activa 6G', 'Honda', 'SCOOTER', 'AUTOMATIC', 'PETROL', 2, 400.00, 'Bangalore', 'AVAILABLE', 'https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?w=800', 'Fuel-efficient daily commuter scooter.', 'KA02GG7007', 2022, 4.4, NOW(), NOW()),
('Mustang GT', 'Ford', 'CONVERTIBLE', 'AUTOMATIC', 'PETROL', 4, 12000.00, 'Mumbai', 'AVAILABLE', 'https://images.unsplash.com/photo-1494905998402-395d579af36f?w=800', 'Roar of the V8 — the iconic muscle car.', 'MH03HH8008', 2023, 4.9, NOW(), NOW());
