-- ============================================================
-- Parent categories
-- ============================================================
INSERT INTO categories (name, slug, description, sort_order) VALUES
    ('Food',    'food',    'Pet food and nutrition',      1),
    ('Toys',    'toys',    'Toys and playtime accessories', 2),
    ('Apparel', 'apparel', 'Clothing and accessories',    3),
    ('Meds',    'meds',    'Medicines and supplements',   4),
    ('Bath',    'bath',    'Grooming and bathing',        5),
    ('Bedding', 'bedding', 'Beds, crates and comfort',   6),
    ('Litter',  'litter',  'Litter trays and sand',      7)
ON CONFLICT (slug) WHERE deleted_at IS NULL DO NOTHING;

-- ============================================================
-- Sub-categories under Food
-- ============================================================
INSERT INTO categories (parent_id, name, slug, description, sort_order)
SELECT c.id, 'Dry Food', 'dry-food', 'Kibble and dry meals', 1
FROM   categories c WHERE c.slug = 'food' AND c.deleted_at IS NULL
ON CONFLICT (slug) WHERE deleted_at IS NULL DO NOTHING;

INSERT INTO categories (parent_id, name, slug, description, sort_order)
SELECT c.id, 'Wet Food', 'wet-food', 'Pouches, pâtés and canned meals', 2
FROM   categories c WHERE c.slug = 'food' AND c.deleted_at IS NULL
ON CONFLICT (slug) WHERE deleted_at IS NULL DO NOTHING;

INSERT INTO categories (parent_id, name, slug, description, sort_order)
SELECT c.id, 'Treats', 'treats', 'Snacks and training rewards', 3
FROM   categories c WHERE c.slug = 'food' AND c.deleted_at IS NULL
ON CONFLICT (slug) WHERE deleted_at IS NULL DO NOTHING;

-- ============================================================
-- Sub-categories under Toys
-- ============================================================
INSERT INTO categories (parent_id, name, slug, description, sort_order)
SELECT c.id, 'Chew Toys', 'chew-toys', 'Chew and tug toys', 1
FROM   categories c WHERE c.slug = 'toys' AND c.deleted_at IS NULL
ON CONFLICT (slug) WHERE deleted_at IS NULL DO NOTHING;

INSERT INTO categories (parent_id, name, slug, description, sort_order)
SELECT c.id, 'Interactive', 'interactive-toys', 'Puzzle feeders and interactive play', 2
FROM   categories c WHERE c.slug = 'toys' AND c.deleted_at IS NULL
ON CONFLICT (slug) WHERE deleted_at IS NULL DO NOTHING;

-- ============================================================
-- Products — Dry Food
-- ============================================================
INSERT INTO products
    (category_id, sku, name, slug, brand, subtitle, description, price, mrp, stock_qty, is_active, is_bestseller, rating, review_count, subscription_eligible)
SELECT c.id,
    'DRY-001', 'Royal Canin Adult', 'royal-canin-adult', 'Royal Canin',
    '3 kg · Adult dogs',
    'Complete nutrition for adult Labradors and Golden Retrievers. Rich in proteins to support muscle mass.',
    1299.00, 1599.00, 80, true, true, 4.60, 1240, true
FROM categories c WHERE c.slug = 'dry-food' AND c.deleted_at IS NULL
ON CONFLICT (sku) WHERE deleted_at IS NULL DO NOTHING;

INSERT INTO products
    (category_id, sku, name, slug, brand, subtitle, description, price, mrp, stock_qty, is_active, is_bestseller, rating, review_count, subscription_eligible)
SELECT c.id,
    'DRY-002', 'Pedigree Adult Chicken', 'pedigree-adult-chicken', 'Pedigree',
    '10 kg · Adult dogs',
    'High-quality chicken and vegetables. Supports healthy skin, shiny coat and strong bones.',
    1849.00, 2199.00, 120, true, false, 4.30, 876, true
FROM categories c WHERE c.slug = 'dry-food' AND c.deleted_at IS NULL
ON CONFLICT (sku) WHERE deleted_at IS NULL DO NOTHING;

INSERT INTO products
    (category_id, sku, name, slug, brand, subtitle, description, price, mrp, stock_qty, is_active, is_bestseller, rating, review_count, subscription_eligible)
SELECT c.id,
    'DRY-003', 'Drools Puppy Starter', 'drools-puppy-starter', 'Drools',
    '1.2 kg · Puppy',
    'Specially formulated for puppies 0–3 months. DHA supports brain development.',
    549.00, 699.00, 60, true, false, 4.50, 412, false
FROM categories c WHERE c.slug = 'dry-food' AND c.deleted_at IS NULL
ON CONFLICT (sku) WHERE deleted_at IS NULL DO NOTHING;

INSERT INTO products
    (category_id, sku, name, slug, brand, subtitle, description, price, mrp, stock_qty, is_active, is_bestseller, rating, review_count, subscription_eligible)
SELECT c.id,
    'DRY-004', 'Hill''s Science Diet', 'hills-science-diet', 'Hill''s',
    '7.5 kg · Adult dogs · Indoor',
    'Clinically proven antioxidants for a strong immune system. Controlled minerals for urinary health.',
    3799.00, 4499.00, 35, true, false, 4.70, 234, true
FROM categories c WHERE c.slug = 'dry-food' AND c.deleted_at IS NULL
ON CONFLICT (sku) WHERE deleted_at IS NULL DO NOTHING;

INSERT INTO products
    (category_id, sku, name, slug, brand, subtitle, description, price, mrp, stock_qty, is_active, is_bestseller, rating, review_count, subscription_eligible)
SELECT c.id,
    'DRY-005', 'Me-O Adult Cat', 'meo-adult-cat', 'Me-O',
    '1.2 kg · Adult cats · Seafood',
    'Balanced diet for adult cats. Taurine for heart and vision health.',
    299.00, 349.00, 200, true, true, 4.40, 689, false
FROM categories c WHERE c.slug = 'dry-food' AND c.deleted_at IS NULL
ON CONFLICT (sku) WHERE deleted_at IS NULL DO NOTHING;

-- ============================================================
-- Products — Wet Food
-- ============================================================
INSERT INTO products
    (category_id, sku, name, slug, brand, subtitle, description, price, mrp, stock_qty, is_active, is_bestseller, rating, review_count, subscription_eligible)
SELECT c.id,
    'WET-001', 'Whiskas Salmon Pâté', 'whiskas-salmon-pate', 'Whiskas',
    '85 g · Adult cats · Pouch',
    'Tender salmon pâté rich in Omega-3. No artificial colours or flavours.',
    45.00, 55.00, 500, true, true, 4.80, 980, false
FROM categories c WHERE c.slug = 'wet-food' AND c.deleted_at IS NULL
ON CONFLICT (sku) WHERE deleted_at IS NULL DO NOTHING;

INSERT INTO products
    (category_id, sku, name, slug, brand, subtitle, description, price, mrp, stock_qty, is_active, is_bestseller, rating, review_count, subscription_eligible)
SELECT c.id,
    'WET-002', 'Pedigree Chicken Chunks', 'pedigree-chicken-chunks', 'Pedigree',
    '130 g · Adult dogs · Gravy',
    'Juicy chicken chunks in rich gravy. 100% complete and balanced for adult dogs.',
    65.00, 80.00, 400, true, false, 4.20, 542, false
FROM categories c WHERE c.slug = 'wet-food' AND c.deleted_at IS NULL
ON CONFLICT (sku) WHERE deleted_at IS NULL DO NOTHING;

INSERT INTO products
    (category_id, sku, name, slug, brand, subtitle, description, price, mrp, stock_qty, is_active, is_bestseller, rating, review_count, subscription_eligible)
SELECT c.id,
    'WET-003', 'Royal Canin Instinctive', 'royal-canin-instinctive', 'Royal Canin',
    '85 g · Cats 1–7 yrs · Gravy',
    'Highly palatable formula designed to meet the instinctive preference of cats aged 1–7 years.',
    110.00, 130.00, 300, true, false, 4.60, 321, false
FROM categories c WHERE c.slug = 'wet-food' AND c.deleted_at IS NULL
ON CONFLICT (sku) WHERE deleted_at IS NULL DO NOTHING;

-- ============================================================
-- Products — Treats
-- ============================================================
INSERT INTO products
    (category_id, sku, name, slug, brand, subtitle, description, price, mrp, stock_qty, is_active, is_bestseller, rating, review_count, subscription_eligible)
SELECT c.id,
    'TRT-001', 'Drools Chicken Jerky', 'drools-chicken-jerky', 'Drools',
    '200 g · All breeds',
    'Tender chicken strips with no artificial additives. High protein training reward.',
    399.00, 499.00, 150, true, true, 4.70, 780, false
FROM categories c WHERE c.slug = 'treats' AND c.deleted_at IS NULL
ON CONFLICT (sku) WHERE deleted_at IS NULL DO NOTHING;

INSERT INTO products
    (category_id, sku, name, slug, brand, subtitle, description, price, mrp, stock_qty, is_active, is_bestseller, rating, review_count, subscription_eligible)
SELECT c.id,
    'TRT-002', 'Pedigree Dentastix', 'pedigree-dentastix', 'Pedigree',
    '180 g · Medium dogs · Dental',
    'Clinically proven to reduce tartar build-up by up to 80%. X-shape cleans hard-to-reach teeth.',
    249.00, 299.00, 200, true, false, 4.50, 632, false
FROM categories c WHERE c.slug = 'treats' AND c.deleted_at IS NULL
ON CONFLICT (sku) WHERE deleted_at IS NULL DO NOTHING;

INSERT INTO products
    (category_id, sku, name, slug, brand, subtitle, description, price, mrp, stock_qty, is_active, is_bestseller, rating, review_count, subscription_eligible)
SELECT c.id,
    'TRT-003', 'Goodies Calcium Bones', 'goodies-calcium-bones', 'Goodies',
    '500 g · Puppies and adults',
    'Milk-flavoured calcium bones. Supports healthy teeth and bones during growth.',
    179.00, 199.00, 300, true, false, 4.30, 415, false
FROM categories c WHERE c.slug = 'treats' AND c.deleted_at IS NULL
ON CONFLICT (sku) WHERE deleted_at IS NULL DO NOTHING;

-- ============================================================
-- Products — Chew Toys
-- ============================================================
INSERT INTO products
    (category_id, sku, name, slug, brand, subtitle, description, price, mrp, stock_qty, is_active, is_bestseller, rating, review_count, subscription_eligible)
SELECT c.id,
    'TOY-CHW-001', 'Cotton Chew Rope', 'cotton-chew-rope', 'PawPlay',
    'Medium · Cotton blend',
    'Durable cotton rope for tugging and chewing. Helps clean teeth and gums during play.',
    249.00, 299.00, 180, true, true, 4.50, 98, false
FROM categories c WHERE c.slug = 'chew-toys' AND c.deleted_at IS NULL
ON CONFLICT (sku) WHERE deleted_at IS NULL DO NOTHING;

INSERT INTO products
    (category_id, sku, name, slug, brand, subtitle, description, price, mrp, stock_qty, is_active, is_bestseller, rating, review_count, subscription_eligible)
SELECT c.id,
    'TOY-CHW-002', 'Rubber Squeaky Ball Set', 'rubber-squeaky-ball-set', 'Petsport',
    'Set of 3 · Natural rubber',
    'Non-toxic natural rubber squeaky balls. Satisfies chewing instinct and keeps dogs entertained.',
    349.00, 399.00, 220, true, false, 4.60, 213, false
FROM categories c WHERE c.slug = 'chew-toys' AND c.deleted_at IS NULL
ON CONFLICT (sku) WHERE deleted_at IS NULL DO NOTHING;

INSERT INTO products
    (category_id, sku, name, slug, brand, subtitle, description, price, mrp, stock_qty, is_active, is_bestseller, rating, review_count, subscription_eligible)
SELECT c.id,
    'TOY-CHW-003', 'KONG Classic Red', 'kong-classic-red', 'KONG',
    'Medium · Natural rubber · Stuffable',
    'World''s #1 dog toy. Stuff with treats or peanut butter for extended play. Dishwasher safe.',
    799.00, 999.00, 90, true, true, 4.90, 1560, false
FROM categories c WHERE c.slug = 'chew-toys' AND c.deleted_at IS NULL
ON CONFLICT (sku) WHERE deleted_at IS NULL DO NOTHING;

-- ============================================================
-- Products — Interactive Toys
-- ============================================================
INSERT INTO products
    (category_id, sku, name, slug, brand, subtitle, description, price, mrp, stock_qty, is_active, is_bestseller, rating, review_count, subscription_eligible)
SELECT c.id,
    'TOY-INT-001', 'Puzzle Feeder Level 2', 'puzzle-feeder-level-2', 'Nina Ottosson',
    'Level 2 · Plastic · Washable',
    'Intermediate difficulty puzzle toy that slows eating and provides mental stimulation.',
    599.00, 799.00, 70, true, false, 4.40, 156, false
FROM categories c WHERE c.slug = 'interactive-toys' AND c.deleted_at IS NULL
ON CONFLICT (sku) WHERE deleted_at IS NULL DO NOTHING;

INSERT INTO products
    (category_id, sku, name, slug, brand, subtitle, description, price, mrp, stock_qty, is_active, is_bestseller, rating, review_count, subscription_eligible)
SELECT c.id,
    'TOY-INT-002', 'Rotating Laser Toy', 'rotating-laser-toy', 'PetSafe',
    'Auto · USB rechargeable · Cat',
    'Automatic rotating laser with 5 speed settings. Keeps cats active for hours. Overheat protection.',
    449.00, 599.00, 110, true, false, 4.70, 87, false
FROM categories c WHERE c.slug = 'interactive-toys' AND c.deleted_at IS NULL
ON CONFLICT (sku) WHERE deleted_at IS NULL DO NOTHING;

INSERT INTO products
    (category_id, sku, name, slug, brand, subtitle, description, price, mrp, stock_qty, is_active, is_bestseller, rating, review_count, subscription_eligible)
SELECT c.id,
    'TOY-INT-003', 'Snuffle Mat', 'snuffle-mat', 'Paw5',
    '35×45 cm · Felt · Washable',
    'Foraging mat that mimics natural sniffing behaviour. Reduces mealtime speed and anxiety.',
    799.00, 999.00, 55, true, false, 4.80, 203, false
FROM categories c WHERE c.slug = 'interactive-toys' AND c.deleted_at IS NULL
ON CONFLICT (sku) WHERE deleted_at IS NULL DO NOTHING;

-- ============================================================
-- Products — Bath & Grooming
-- ============================================================
INSERT INTO products
    (category_id, sku, name, slug, brand, subtitle, description, price, mrp, stock_qty, is_active, is_bestseller, rating, review_count, subscription_eligible)
SELECT c.id,
    'BATH-001', 'Himalaya Erina-EP Shampoo', 'himalaya-erina-ep-shampoo', 'Himalaya',
    '200 ml · All coats',
    'Tick and flea protection shampoo with neem and eucalyptus. Suitable for dogs and cats.',
    299.00, 349.00, 250, true, true, 4.40, 890, false
FROM categories c WHERE c.slug = 'bath' AND c.deleted_at IS NULL
ON CONFLICT (sku) WHERE deleted_at IS NULL DO NOTHING;

INSERT INTO products
    (category_id, sku, name, slug, brand, subtitle, description, price, mrp, stock_qty, is_active, is_bestseller, rating, review_count, subscription_eligible)
SELECT c.id,
    'BATH-002', 'Wahl Pet Nail Grinder', 'wahl-pet-nail-grinder', 'Wahl',
    'USB rechargeable · Low noise',
    'Professional-grade nail grinder with 2 speeds. Diamond-bit head for smooth finish. 60-min battery.',
    1299.00, 1599.00, 45, true, false, 4.60, 312, false
FROM categories c WHERE c.slug = 'bath' AND c.deleted_at IS NULL
ON CONFLICT (sku) WHERE deleted_at IS NULL DO NOTHING;

-- ============================================================
-- Groomers — stored as vets with specialty = 'GROOMING'
-- ============================================================
INSERT INTO vets
    (display_name, specialty, bio, status, rating, review_count, years_experience,
     home_visit_available, clinic_name, city, state, pincode)
SELECT 'Pawfect Spa', 'GROOMING',
     'Full-service grooming salon offering bath, haircut, nail trim and ear cleaning for dogs and cats.',
     'ACTIVE', 4.80, 312, 5, false, 'Pawfect Spa', 'Ghaziabad', 'Uttar Pradesh', '201014'
WHERE NOT EXISTS (SELECT 1 FROM vets WHERE display_name = 'Pawfect Spa' AND deleted_at IS NULL);

INSERT INTO vets
    (display_name, specialty, bio, status, rating, review_count, years_experience,
     home_visit_available, clinic_name, city, state, pincode)
SELECT 'The Fluffy Salon', 'GROOMING',
     'Specialising in breed-specific cuts, de-shedding and full groom packages.',
     'ACTIVE', 4.60, 187, 3, false, 'The Fluffy Salon', 'Ghaziabad', 'Uttar Pradesh', '201012'
WHERE NOT EXISTS (SELECT 1 FROM vets WHERE display_name = 'The Fluffy Salon' AND deleted_at IS NULL);

INSERT INTO vets
    (display_name, specialty, bio, status, rating, review_count, years_experience,
     home_visit_available, clinic_name, city, state, pincode)
SELECT 'Snip & Wag', 'GROOMING',
     'Gentle grooming for anxious pets. Bath, blow-dry, teeth brushing and paw care.',
     'ACTIVE', 4.70, 94, 4, false, 'Snip & Wag', 'Noida', 'Uttar Pradesh', '201301'
WHERE NOT EXISTS (SELECT 1 FROM vets WHERE display_name = 'Snip & Wag' AND deleted_at IS NULL);

INSERT INTO vets
    (display_name, specialty, bio, status, rating, review_count, years_experience,
     home_visit_available, clinic_name, city, state, pincode)
SELECT 'Posh Paws', 'GROOMING',
     'Premium grooming experience with organic products. Massage, breed cut and spa treatments.',
     'ACTIVE', 4.50, 56, 6, true, 'Posh Paws', 'Ghaziabad', 'Uttar Pradesh', '201016'
WHERE NOT EXISTS (SELECT 1 FROM vets WHERE display_name = 'Posh Paws' AND deleted_at IS NULL);
