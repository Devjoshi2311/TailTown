-- ============================================================
-- Products seeded in V13 never got an image_url — every product card,
-- detail page, and cart line item was rendering a blank placeholder.
-- ============================================================
UPDATE products SET image_url = 'https://picsum.photos/seed/royal-canin-adult/200/200'
    WHERE sku = 'DRY-001' AND image_url IS NULL AND deleted_at IS NULL;
UPDATE products SET image_url = 'https://picsum.photos/seed/pedigree-adult-chicken/200/200'
    WHERE sku = 'DRY-002' AND image_url IS NULL AND deleted_at IS NULL;
UPDATE products SET image_url = 'https://picsum.photos/seed/drools-puppy-starter/200/200'
    WHERE sku = 'DRY-003' AND image_url IS NULL AND deleted_at IS NULL;
UPDATE products SET image_url = 'https://picsum.photos/seed/hills-science-diet/200/200'
    WHERE sku = 'DRY-004' AND image_url IS NULL AND deleted_at IS NULL;
UPDATE products SET image_url = 'https://picsum.photos/seed/meo-adult-cat/200/200'
    WHERE sku = 'DRY-005' AND image_url IS NULL AND deleted_at IS NULL;

UPDATE products SET image_url = 'https://picsum.photos/seed/whiskas-salmon-pate/200/200'
    WHERE sku = 'WET-001' AND image_url IS NULL AND deleted_at IS NULL;
UPDATE products SET image_url = 'https://picsum.photos/seed/pedigree-chicken-chunks/200/200'
    WHERE sku = 'WET-002' AND image_url IS NULL AND deleted_at IS NULL;
UPDATE products SET image_url = 'https://picsum.photos/seed/royal-canin-instinctive/200/200'
    WHERE sku = 'WET-003' AND image_url IS NULL AND deleted_at IS NULL;

UPDATE products SET image_url = 'https://picsum.photos/seed/drools-chicken-jerky-treat/200/200'
    WHERE sku = 'TRT-001' AND image_url IS NULL AND deleted_at IS NULL;
UPDATE products SET image_url = 'https://picsum.photos/seed/pedigree-dentastix/200/200'
    WHERE sku = 'TRT-002' AND image_url IS NULL AND deleted_at IS NULL;
UPDATE products SET image_url = 'https://picsum.photos/seed/goodies-calcium-bones/200/200'
    WHERE sku = 'TRT-003' AND image_url IS NULL AND deleted_at IS NULL;

UPDATE products SET image_url = 'https://picsum.photos/seed/cotton-chew-rope/200/200'
    WHERE sku = 'TOY-CHW-001' AND image_url IS NULL AND deleted_at IS NULL;
UPDATE products SET image_url = 'https://picsum.photos/seed/rubber-squeaky-ball-set/200/200'
    WHERE sku = 'TOY-CHW-002' AND image_url IS NULL AND deleted_at IS NULL;
UPDATE products SET image_url = 'https://picsum.photos/seed/kong-classic-red/200/200'
    WHERE sku = 'TOY-CHW-003' AND image_url IS NULL AND deleted_at IS NULL;

UPDATE products SET image_url = 'https://picsum.photos/seed/puzzle-feeder-level-2/200/200'
    WHERE sku = 'TOY-INT-001' AND image_url IS NULL AND deleted_at IS NULL;
UPDATE products SET image_url = 'https://picsum.photos/seed/rotating-laser-toy/200/200'
    WHERE sku = 'TOY-INT-002' AND image_url IS NULL AND deleted_at IS NULL;
UPDATE products SET image_url = 'https://picsum.photos/seed/snuffle-mat-toy/200/200'
    WHERE sku = 'TOY-INT-003' AND image_url IS NULL AND deleted_at IS NULL;

UPDATE products SET image_url = 'https://picsum.photos/seed/himalaya-erina-shampoo/200/200'
    WHERE sku = 'BATH-001' AND image_url IS NULL AND deleted_at IS NULL;
UPDATE products SET image_url = 'https://picsum.photos/seed/wahl-nail-grinder/200/200'
    WHERE sku = 'BATH-002' AND image_url IS NULL AND deleted_at IS NULL;
