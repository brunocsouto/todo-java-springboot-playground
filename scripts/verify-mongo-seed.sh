#!/usr/bin/env bash
set -euo pipefail

docker compose exec -T mongodb mongosh --quiet todo \
  --file /docker-entrypoint-initdb.d/mongo-seed.js

docker compose exec -T mongodb mongosh --quiet todo <<'MONGOSH'
const expectedCategories = 100;
const expectedFolders = 100;
const expectedTodos = 1000;

const failures = [];

function assert(condition, message) {
  if (!condition) failures.push(message);
}

assert(db.categories.countDocuments({ _id: /^category-/ }) === expectedCategories,
  "Expected 100 categories");
assert(db.folders.countDocuments({ _id: /^folder-/ }) === expectedFolders,
  "Expected 100 folders");
assert(db.todos.countDocuments({ _id: /^todo-/ }) === expectedTodos,
  "Expected 1000 todos");

assert(db.categories.countDocuments({ _id: "category-001", name: "Category 001" }) === 1,
  "Category deterministic ID or name is invalid");
assert(db.folders.countDocuments({ _id: "folder-001", name: "Folder 001" }) === 1,
  "Folder deterministic ID or name is invalid");
assert(db.todos.countDocuments({
  _id: "todo-0001",
  title: "Todo 0001",
  "category.$id": "category-001",
  "folder.$id": "folder-001",
}) === 1, "Todo deterministic ID or relationship is invalid");

assert(db.todos.countDocuments({
  _id: /^todo-/,
  $or: [
    { "category.$id": { $nin: db.categories.find({ _id: /^category-/ }, { _id: 1 }).toArray().map(c => c._id) } },
    { "folder.$id": { $nin: db.folders.find({ _id: /^folder-/ }, { _id: 1 }).toArray().map(f => f._id) } },
  ],
}) === 0, "A seeded todo references a missing document");

if (failures.length > 0) {
  print(`MongoDB seed verification failed:\n- ${failures.join("\n- ")}`);
  quit(1);
}

print("MongoDB seed verification passed, including idempotency.");
MONGOSH
