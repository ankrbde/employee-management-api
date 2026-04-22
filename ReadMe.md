TODO: Implement cursor-based pagination for scalable listing

- Replace offset pagination with cursor (based on id or createdAt)
- Use "WHERE id > last_seen_id" pattern
- Ensure stable ordering (ORDER BY id or createdAt)
- Return nextCursor in API response
- Handle edge cases (deleted records, duplicates)