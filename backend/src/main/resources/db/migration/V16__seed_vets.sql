-- ============================================================
-- Vets — actual veterinarians (as opposed to the GROOMING salons
-- seeded in V13, which are grooming businesses, not vets, and are
-- now excluded from the default /api/v1/vets listing).
-- ============================================================
INSERT INTO vets
    (display_name, specialty, bio, avatar_url, status, rating, review_count, years_experience,
     home_visit_available, clinic_name, city, state, pincode)
SELECT 'Dr. Priya Sharma', 'Small Animal Medicine',
     'BVSc & AH from GADVASU with 9 years in small animal internal medicine. Special interest in feline gastroenterology and canine endocrinology.',
     'https://randomuser.me/api/portraits/women/48.jpg',
     'ACTIVE', 4.80, 312, 9, true, 'PawsCare Clinic', 'Delhi', 'Delhi', '110029'
WHERE NOT EXISTS (SELECT 1 FROM vets WHERE display_name = 'Dr. Priya Sharma' AND deleted_at IS NULL);

INSERT INTO vets
    (display_name, specialty, bio, avatar_url, status, rating, review_count, years_experience,
     home_visit_available, clinic_name, city, state, pincode)
SELECT 'Dr. Arjun Mehta', 'Veterinary Surgery',
     'MVSc Surgery from MAFSU. Specialist in orthopaedic and soft tissue surgery for dogs and cats. Has performed over 1,200 procedures.',
     'https://randomuser.me/api/portraits/men/32.jpg',
     'ACTIVE', 4.90, 487, 12, false, 'Mehta Veterinary Hospital', 'Mumbai', 'Maharashtra', '400028'
WHERE NOT EXISTS (SELECT 1 FROM vets WHERE display_name = 'Dr. Arjun Mehta' AND deleted_at IS NULL);

INSERT INTO vets
    (display_name, specialty, bio, avatar_url, status, rating, review_count, years_experience,
     home_visit_available, clinic_name, city, state, pincode)
SELECT 'Dr. Ananya Reddy', 'Exotic & Avian Animals',
     'One of Bangalore''s few certified exotic animal vets. Handles rabbits, guinea pigs, birds, reptiles, and small mammals alongside dogs and cats.',
     'https://randomuser.me/api/portraits/women/44.jpg',
     'ACTIVE', 4.70, 198, 7, true, 'Wildside Animal Clinic', 'Bangalore', 'Karnataka', '560038'
WHERE NOT EXISTS (SELECT 1 FROM vets WHERE display_name = 'Dr. Ananya Reddy' AND deleted_at IS NULL);

INSERT INTO vets
    (display_name, specialty, bio, avatar_url, status, rating, review_count, years_experience,
     home_visit_available, clinic_name, city, state, pincode)
SELECT 'Dr. Kabir Singh', 'Veterinary Dermatology',
     'Specialises in skin disorders, allergies, and coat conditions in dogs and cats. Published research on atopic dermatitis in Indian Spitz breeds.',
     'https://randomuser.me/api/portraits/men/68.jpg',
     'ACTIVE', 4.60, 143, 8, false, 'DermaPet Clinic', 'Delhi', 'Delhi', '110049'
WHERE NOT EXISTS (SELECT 1 FROM vets WHERE display_name = 'Dr. Kabir Singh' AND deleted_at IS NULL);

INSERT INTO vets
    (display_name, specialty, bio, avatar_url, status, rating, review_count, years_experience,
     home_visit_available, clinic_name, city, state, pincode)
SELECT 'Dr. Meera Iyer', 'Internal Medicine',
     'BVSc from TANUVAS, MVSc Internal Medicine from IVRI. Focuses on complex diagnostic cases — cardiac, renal, and hepatic disease in companion animals.',
     'https://randomuser.me/api/portraits/women/65.jpg',
     'ACTIVE', 4.90, 271, 6, true, 'Iyer Pet Hospital', 'Chennai', 'Tamil Nadu', '600004'
WHERE NOT EXISTS (SELECT 1 FROM vets WHERE display_name = 'Dr. Meera Iyer' AND deleted_at IS NULL);

-- ============================================================
-- Backfill avatars for the V13 grooming salons, which were seeded
-- without one — their cards render with a plain colour block otherwise.
-- ============================================================
UPDATE vets SET avatar_url = 'https://randomuser.me/api/portraits/women/12.jpg'
    WHERE display_name = 'Pawfect Spa' AND avatar_url IS NULL AND deleted_at IS NULL;
UPDATE vets SET avatar_url = 'https://randomuser.me/api/portraits/women/23.jpg'
    WHERE display_name = 'The Fluffy Salon' AND avatar_url IS NULL AND deleted_at IS NULL;
UPDATE vets SET avatar_url = 'https://randomuser.me/api/portraits/men/15.jpg'
    WHERE display_name = 'Snip & Wag' AND avatar_url IS NULL AND deleted_at IS NULL;
UPDATE vets SET avatar_url = 'https://randomuser.me/api/portraits/women/33.jpg'
    WHERE display_name = 'Posh Paws' AND avatar_url IS NULL AND deleted_at IS NULL;
