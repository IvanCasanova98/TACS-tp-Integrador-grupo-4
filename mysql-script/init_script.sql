CREATE DATABASE IF NOT EXISTS tacs;

CREATE TABLE IF NOT EXISTS `players` (
  `id` varchar(255) PRIMARY KEY,
  `username` varchar(255),
  `image_url` longtext,
  `is_admin` boolean,
  `is_blocked` boolean
);

CREATE TABLE IF NOT EXISTS `decks` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(255),
  `card_ids` varchar(255),
  `deleted` boolean DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS `matches` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `status` varchar(255),
  `creator_id` varchar(255),
  `challenged_user_id` varchar(255),
  `deck_id` int,
  `winner_id` varchar(255),
  `created_date` datetime,
  FOREIGN KEY (`creator_id`) REFERENCES `players` (`id`),
  FOREIGN KEY (`challenged_user_id`) REFERENCES `players` (`id`),
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
  FOREIGN KEY (`match_id`) REFERENCES `matches` (`id`)
);

CREATE USER 'superfriends_app'@'database' IDENTIFIED BY 'batman';
GRANT ALL PRIVILEGES ON *.* TO superfriends_app@database;

INSERT INTO decks(name, card_ids) values ("Primer mazo", "10,11,12,13");
INSERT INTO decks(name, card_ids) values ("Batman super mazo", "1,8,4,3");
INSERT INTO decks(name, card_ids) values ("Mazo 3", "20,1,2,3,13");
INSERT INTO decks(name, card_ids) values ("Another deck", "1,2,4,14");
INSERT INTO decks(name, card_ids) values ("A-bomb mazo", "11,2,3,4");
INSERT INTO decks(name, card_ids) values ("Batman super deck", "24,1,4,3,2");

INSERT INTO players(id,username,image_url,is_admin,is_blocked) values ("104725077753706905086", "Franco Giannotti", "https://lh3.googleusercontent.com/a-/AOh14GgjwE38QY3xY6yljKclSoVRnByF-59pAG1wdvx_=s96-c", true, false);
INSERT INTO players(id,username,image_url,is_admin,is_blocked) values ("104065320855221322833", "Julieta Abuin", "https://lh3.googleusercontent.com/a-/AOh14Gh5tYvnhd0arKFn9ot7FU6D6mrnSpfuh6_hAPvMsg=s96-c", true, false);
INSERT INTO players(id,username,image_url,is_admin,is_blocked) values ("102400486230688279463", "FRANCO GIANNOTTI CALENS", "https://lh3.googleusercontent.com/a-/AOh14GgjwE38QY3xY6yljKclSoVRnByF-59pAG1wdvx_=s96-c", true, false);
INSERT INTO players(id,username,image_url,is_admin,is_blocked) values ("107032331312948829616", "Chiara M", "https://lh3.googleusercontent.com/a-/AOh14Gh5tYvnhd0arKFn9ot7FU6D6mrnSpfuh6_hAPvMsg=s96-c", true, false);
INSERT INTO players(id,username,image_url,is_admin,is_blocked) values ("115748028387079548757", "Ivan C", "https://lh3.googleusercontent.com/a-/AOh14Gh5tYvnhd0arKFn9ot7FU6D6mrnSpfuh6_hAPvMsg=s96-c", true, false);
INSERT INTO players(id, username, image_url, is_admin, is_blocked) values ("107090515790711287955", "Julieta Lucia Abuin", "https://lh3.googleusercontent.com/a-/AOh14Gh5tYvnhd0arKFn9ot7FU6D6mrnSpfuh6_hAPvMsg=s96-c", true, false);
INSERT INTO players(id, username, image_url, is_admin, is_blocked) values ("automatedPlayer", "Batman", "https://findicons.com/files/icons/1293/the_batman_vol_1/256/batman.png", true, false);

INSERT INTO matches(id, status, creator_id, challenged_user_id, deck_id, winner_id, created_date) values (1, "FINISHED", "104065320855221322833", "104725077753706905086", 3, "104065320855221322833", CURRENT_DATE);
INSERT INTO matches(id, status, creator_id, challenged_user_id, deck_id, winner_id, created_date) values (2, "PAUSED", "102400486230688279463", "104065320855221322833", 1, null, CURRENT_DATE);

INSERT INTO movements(match_id, attribute_name, creator_card_id, opponent_card_id, winner_id_or_tie, turn) VALUES (1, "STRENGTH", 2, 4, "104065320855221322833", "104065320855221322833");