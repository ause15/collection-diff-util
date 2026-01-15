# collection-diff-util

A lightweight Java utility for synchronizing full-list updates  
(insert / update / delete) between frontend submissions and database records.

---

## ✨ Why this exists

In many backend systems, the frontend submits the **entire list** of records,
while the database already contains existing data.

Typical examples:

- User ↔ Role relationships
- Order ↔ Item relationships
- Tag / Permission / Configuration updates

A naive solution is:

```text
DELETE all
INSERT all
