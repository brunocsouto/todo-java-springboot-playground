const categories = db.getSiblingDB("todo").categories;
const folders = db.getSiblingDB("todo").folders;
const todos = db.getSiblingDB("todo").todos;

const workCategoryId = "11111111-1111-1111-1111-111111111111";
const personalCategoryId = "22222222-2222-2222-2222-222222222222";
const workFolderId = "33333333-3333-3333-3333-333333333333";
const homeFolderId = "44444444-4444-4444-4444-444444444444";

categories.updateOne(
  { _id: workCategoryId },
  { $set: { name: "Work" } },
  { upsert: true }
);
categories.updateOne(
  { _id: personalCategoryId },
  { $set: { name: "Personal" } },
  { upsert: true }
);

folders.updateOne(
  { _id: workFolderId },
  { $set: { name: "Work tasks" } },
  { upsert: true }
);
folders.updateOne(
  { _id: homeFolderId },
  { $set: { name: "Home tasks" } },
  { upsert: true }
);

todos.updateOne(
  { title: "Review API documentation" },
  {
    $set: {
      description: "Check every endpoint and update the examples.",
      category: DBRef("categories", workCategoryId),
      folder: DBRef("folders", workFolderId),
    },
  },
  { upsert: true }
);
todos.updateOne(
  { title: "Plan the weekend" },
  {
    $set: {
      description: "Choose activities and prepare a short checklist.",
      category: DBRef("categories", personalCategoryId),
      folder: DBRef("folders", homeFolderId),
    },
  },
  { upsert: true }
);
