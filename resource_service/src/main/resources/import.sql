# For each database:
ALTER DATABASE dressme CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

# For each table:
ALTER TABLE abs_location CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
# For each column:
ALTER TABLE abs_location CHANGE city_name city_name VARCHAR(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE abs_location CHANGE name name VARCHAR(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE comment CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE comment CHANGE content content VARCHAR(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE external_account CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE external_account CHANGE username username VARCHAR(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE post CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE post CHANGE description description VARCHAR(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE post_feedback_possibility CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE post_feedback_possibility CHANGE name name VARCHAR(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE user CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE user CHANGE username username VARCHAR(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

SET NAMES utf8mb4;
SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;