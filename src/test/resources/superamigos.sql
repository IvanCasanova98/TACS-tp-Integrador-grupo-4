CREATE DATABASE IF NOT EXISTS tacs;

CREATE TABLE `players` (
  `id` varchar(255) PRIMARY KEY,
  `username` varchar(255),
  `image_url` varchar(255),
  `is_admin` boolean,
  `is_blocked` boolean
);

CREATE TABLE `matches` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `status` varchar(255),
  `creator_id` varchar(255),
  `challenged_user_id` varchar(255),
  `deck_id` int,
  `winner_id` varchar(255),
  `created_date` datetime
);

CREATE TABLE `decks` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(255),
  `card_ids` varchar(255)
);

CREATE TABLE `movements` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `match_id` int,
  `attribute_name` varchar(255),
  `creator_card_id` int,
  `opponent_card_id` int,
  `winner_id_or_tie` varchar(255),
  `turn` varchar(255)
);

ALTER TABLE `matches` ADD FOREIGN KEY (`creator_id`) REFERENCES `players` (`id`);

ALTER TABLE `matches` ADD FOREIGN KEY (`challenged_user_id`) REFERENCES `players` (`id`);

ALTER TABLE `matches` ADD FOREIGN KEY (`deck_id`) REFERENCES `decks` (`id`);

ALTER TABLE `movements` ADD FOREIGN KEY (`match_id`) REFERENCES `matches` (`id`);
