for i in {1..10}; do
  curl -X POST http://localhost:8080/employees \
    -H "Content-Type: application/json" \
    -d "{
      \"name\": \"Test User $i\",
      \"email\": \"test$i@example.com\"
    }"
done
