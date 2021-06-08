CREATE DATABASE IF NOT EXISTS tacs;

CREATE TABLE IF NOT EXISTS `players` (
  `id` varchar(255) PRIMARY KEY,
  `username` varchar(255),
  `image_url` varchar(255),
  `is_admin` boolean,
  `is_blocked` boolean
);

CREATE TABLE IF NOT EXISTS `decks` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(255),
  `card_ids` varchar(255)
);

CREATE TABLE IF NOT EXISTS `match` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `status` varchar(255),
  `creator_id` varchar(255),
  `challenged_user_id` varchar(255),
  `deck_id` int,
  `winner_id` varchar(255),
  `created_date` datetime,
  FOREIGN KEY (`creator_id`) REFERENCES `players` (`id`),
  FOREIGN KEY (`challenged_user_id`) REFERENCES `players` (`id`),
  FOREIGN KEY (`winner_id`) REFERENCES `players` (`id`),
  FOREIGN KEY (`deck_id`) REFERENCES `decks` (`id`)
);



CREATE TABLE IF NOT EXISTS `movements` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `match_id` int,
  `attribute_name` varchar(255),
  `creator_card_id` int,
  `opponent_card_id` int,
  `winner_id_or_tie` varchar(255),
  `turn` varchar(255),
  FOREIGN KEY (`match_id`) REFERENCES `match` (`id`)
);

CREATE USER 'superfriends_app'@'database' IDENTIFIED BY 'batman';
GRANT ALL PRIVILEGES ON *.* TO superfriends_app@database;