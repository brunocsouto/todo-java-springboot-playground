INSERT INTO folders (id, name)
VALUES
    ('2f45a48a-534d-45f5-8117-ce73608e0c77', 'Personal'),
    ('5a4d155f-616a-49b4-9a31-816b8d925cdc', 'Work'),
    ('ea87ad1d-26d2-45ae-b14c-a9612885b7a9', 'Learning')
ON CONFLICT (id) DO NOTHING;

INSERT INTO categories (id, name)
VALUES
    ('668cf61a-c7e9-4b73-a03c-4c6b61a3347b', 'Planning'),
    ('0faeb261-6cb3-46f8-9f91-7db0f5dc801d', 'Errands'),
    ('5aa71afa-8678-40ed-bb57-c6341046d165', 'Development'),
    ('319a26d3-cdfe-495a-a164-1ecf5f468ab3', 'Reading'),
    ('cbf1b04e-fb92-42c9-a844-421d2ba82db3', 'Health'),
    ('2d6b9a80-f3e0-41b3-9199-f592f6d491a2', 'Finance')
ON CONFLICT (id) DO NOTHING;

INSERT INTO todos (id, title, description, folder_id, category_id)
VALUES
    (
        '2ae70c47-f6d2-4313-bc3e-c9b97fe8e6b3',
        'Plan the week',
        'Review priorities and prepare the schedule for the week.',
        '2f45a48a-534d-45f5-8117-ce73608e0c77',
        '668cf61a-c7e9-4b73-a03c-4c6b61a3347b'
    ),
    (
        'd25c2207-835b-4dde-9cb2-0df7ad4e4d41',
        'Buy groceries',
        'Get vegetables, fruit, coffee, and bread.',
        '2f45a48a-534d-45f5-8117-ce73608e0c77',
        '0faeb261-6cb3-46f8-9f91-7db0f5dc801d'
    ),
    (
        '80f5672f-9375-404e-8bea-2647f424ba91',
        'Implement todo API',
        'Create endpoints for listing, creating, and updating todos.',
        '5a4d155f-616a-49b4-9a31-816b8d925cdc',
        '5aa71afa-8678-40ed-bb57-c6341046d165'
    ),
    (
        '673ef6cc-3f40-4f13-a954-47929a2787ef',
        'Review database migrations',
        NULL,
        '5a4d155f-616a-49b4-9a31-816b8d925cdc',
        '319a26d3-cdfe-495a-a164-1ecf5f468ab3'
    ),
    (
        '986ab4f2-372f-4cc1-8a2e-9077d09d87ff',
        'Schedule a health checkup',
        'Call the clinic and choose an appointment time.',
        '2f45a48a-534d-45f5-8117-ce73608e0c77',
        'cbf1b04e-fb92-42c9-a844-421d2ba82db3'
    ),
    (
        'f56d9bc2-84c9-4d87-8a27-8a4bf9ae3da9',
        'Review monthly budget',
        'Check recurring expenses and update the savings plan.',
        '5a4d155f-616a-49b4-9a31-816b8d925cdc',
        '2d6b9a80-f3e0-41b3-9199-f592f6d491a2'
    )
ON CONFLICT (id) DO NOTHING;
