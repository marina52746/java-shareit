MERGE INTO bookings (id, start_date, end_date, items_id, users_id, status) VALUES
(1, '2023-08-20T09:00:00.000+00:00', '2023-08-30T09:00:00.000+00:00', 7, 1, 'APPROVED'),
(2, '2023-08-10T09:00:00.000+00:00', '2023-08-18T09:00:00.000+00:00', 3, 5, 'REJECTED'),
(3, '2023-08-01T09:00:00.000+00:00', '2023-09-30T09:00:00.000+00:00', 1, 4, 'APPROVED'),
(4, '2023-08-25T09:00:00.000+00:00', '2023-09-25T09:00:00.000+00:00', 4, 4, 'REJECTED'),
(5, '2023-07-10T09:00:00.000+00:00', '2023-07-20T09:00:00.000+00:00', 1, 5, 'APPROVED'),
(6, '2023-07-15T09:00:00.000+00:00', '2023-07-27T09:00:00.000+00:00', 3, 2, 'CANCELED'),
(7, '2023-09-15T09:00:00.000+00:00', '2023-09-26T09:00:00.000+00:00', 2, 1, 'APPROVED'),
(8, '2023-09-26T09:00:00.000+00:00', '2023-09-29T09:00:00.000+00:00', 3, 3, 'WAITING'),
(9, '2023-08-23T09:00:00.000+00:00', '2023-09-17T09:00:00.000+00:00', 5, 1, 'APPROVED'),
(10, '2023-07-22T09:00:00.000+00:00', '2023-07-24T09:00:00.000+00:00', 7, 4, 'WAITING');