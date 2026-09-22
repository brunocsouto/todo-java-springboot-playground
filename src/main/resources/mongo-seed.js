const database = db.getSiblingDB("todo");
const categories = database.categories;
const folders = database.folders;
const todos = database.todos;

const categoryIds = [];
const folderIds = [];

for (let index = 1; index <= 100; index++) {
  const id = `category-${String(index).padStart(3, "0")}`;
  categoryIds.push(id);
  categories.updateOne(
    { _id: id },
    { $set: { name: `Category ${String(index).padStart(3, "0")}` } },
    { upsert: true }
  );
}

for (let index = 1; index <= 100; index++) {
  const id = `folder-${String(index).padStart(3, "0")}`;
  folderIds.push(id);
  folders.updateOne(
    { _id: id },
    { $set: { name: `Folder ${String(index).padStart(3, "0")}` } },
    { upsert: true }
  );
}

for (let index = 1; index <= 1000; index++) {
  const todoNumber = String(index).padStart(4, "0");
  const categoryId = categoryIds[(index - 1) % categoryIds.length];
  const folderId = folderIds[(index - 1) % folderIds.length];

  todos.updateOne(
    { _id: `todo-${todoNumber}` },
    {
      $set: {
        title: `Todo ${todoNumber}`,
        description: `Description for todo ${todoNumber}.`,
        category: DBRef("categories", categoryId),
        folder: DBRef("folders", folderId),
      },
    },
    { upsert: true }
  );
}
