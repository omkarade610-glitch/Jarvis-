const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// 1. Unauthenticated rejection
test("Unauthenticated user: cannot read user profile", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
});

test("Unauthenticated user: cannot write user profile", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).set({
    userId: ALICE_UID,
    assistantName: "JARVIS",
    ownerName: "Tony Stark",
    createdAt: new Date(),
    updatedAt: new Date()
  }));
});

// 2. Cross-user isolation
test("Authenticated user: cannot read another user's projects", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).collection("projects").doc("proj1").set({
      userId: ALICE_UID,
      title: "Iron Suit OS",
      projectType: "coding",
      createdAt: new Date(),
      updatedAt: new Date()
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("projects").doc("proj1").get());
});

test("Authenticated user: cannot write to another user's profile", async () => {
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).set({
    userId: ALICE_UID,
    assistantName: "JARVIS",
    ownerName: "Tony Stark",
    createdAt: new Date(),
    updatedAt: new Date()
  }));
});

// 3. Owner access validation
test("Authenticated user: can create and read own profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).set({
    userId: ALICE_UID,
    assistantName: "JARVIS",
    ownerName: "Tony Stark",
    createdAt: new Date(),
    updatedAt: new Date()
  }));

  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: can create own conversation and message", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("conversations").doc("conv_1").set({
    userId: ALICE_UID,
    title: "Quantum Physics Dialogue",
    createdAt: new Date(),
    updatedAt: new Date()
  }));

  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("conversations").doc("conv_1").collection("messages").doc("msg_1").set({
    userId: ALICE_UID,
    sender: "user",
    text: "Good morning, JARVIS.",
    createdAt: new Date()
  }));
});

test("Authenticated user: can create own project and memory item", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("projects").doc("proj_1").set({
    userId: ALICE_UID,
    title: "Hologram Display v2",
    projectType: "coding",
    createdAt: new Date(),
    updatedAt: new Date()
  }));

  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("memories").doc("mem_1").set({
    userId: ALICE_UID,
    category: "Preferences",
    key: "favorite_color",
    value: "Arc Reactor Blue",
    createdAt: new Date(),
    updatedAt: new Date()
  }));
});
