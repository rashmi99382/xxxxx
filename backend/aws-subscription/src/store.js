import { DynamoDBClient } from "@aws-sdk/client-dynamodb";
import { DeleteCommand, DynamoDBDocumentClient, PutCommand, QueryCommand, ScanCommand, UpdateCommand } from "@aws-sdk/lib-dynamodb";

const client = DynamoDBDocumentClient.from(new DynamoDBClient({}));

function tableName() {
  const table = process.env.DYNAMODB_TABLE_SHOPS;
  if (!table || table === "YOUR_TABLE") throw new Error("DYNAMODB_TABLE_SHOPS is not configured.");
  return table;
}

export async function createShop(shop) {
  await client.send(new PutCommand({
    TableName: tableName(),
    Item: shop,
    ConditionExpression: "attribute_not_exists(shopId)"
  }));
  return shop;
}

export async function listShops() {
  const result = await client.send(new ScanCommand({ TableName: tableName() }));
  return result.Items || [];
}

export async function findShopByEmail(email) {
  const result = await client.send(new QueryCommand({
    TableName: tableName(),
    IndexName: "email-index",
    KeyConditionExpression: "email = :email",
    ExpressionAttributeValues: { ":email": email }
  }));
  return (result.Items || [])[0] || null;
}

export async function findShopById(shopId) {
  const result = await client.send(new ScanCommand({
    TableName: tableName(),
    FilterExpression: "shopId = :shopId",
    ExpressionAttributeValues: { ":shopId": shopId }
  }));
  return (result.Items || [])[0] || null;
}

export async function findShopByRazorpaySubscription(subscriptionId) {
  const result = await client.send(new ScanCommand({
    TableName: tableName(),
    FilterExpression: "razorpaySubscriptionId = :subscriptionId",
    ExpressionAttributeValues: { ":subscriptionId": subscriptionId }
  }));
  return (result.Items || [])[0] || null;
}

export async function updateShop(shopId, fields) {
  const entries = Object.entries({ ...fields, updatedAt: new Date().toISOString() });
  const names = {};
  const values = {};
  const expression = entries.map(([key, value], index) => {
    names[`#k${index}`] = key;
    values[`:v${index}`] = value;
    return `#k${index} = :v${index}`;
  }).join(", ");

  const result = await client.send(new UpdateCommand({
    TableName: tableName(),
    Key: { shopId },
    UpdateExpression: `SET ${expression}`,
    ExpressionAttributeNames: names,
    ExpressionAttributeValues: values,
    ReturnValues: "ALL_NEW"
  }));
  return result.Attributes;
}

export async function removeShop(shopId) {
  await client.send(new DeleteCommand({ TableName: tableName(), Key: { shopId } }));
}
