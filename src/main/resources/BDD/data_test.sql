-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema pay_my_buddy_test
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema pay_my_buddy_test
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `pay_my_buddy_test` DEFAULT CHARACTER SET utf8 ;
USE `pay_my_buddy_test` ;

-- -----------------------------------------------------
-- Table `pay_my_buddy_test`.`user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `pay_my_buddy_test`.`user` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `user_name` VARCHAR(45) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `user_id_UNIQUE` (`user_id` ASC) VISIBLE,
  UNIQUE INDEX `user_name_UNIQUE` (`user_name` ASC) VISIBLE,
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pay_my_buddy_test`.`user_transaction`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `user_transaction`;
CREATE TABLE IF NOT EXISTS `pay_my_buddy_test`.`user_transaction` (
  `transaction_id` INT NOT NULL AUTO_INCREMENT,
  `sender_id` INT NOT NULL,
  `receiver_id` INT NOT NULL,
  `description` VARCHAR(255) NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`transaction_id`),
  INDEX `fk_user_transaction_user1_idx` (`sender_id` ASC) VISIBLE,
  INDEX `fk_user_transaction_user2_idx` (`receiver_id` ASC) VISIBLE,
  CONSTRAINT `fk_user_transaction_user1`
    FOREIGN KEY (`sender_id`)
    REFERENCES `pay_my_buddy_test`.`user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_transaction_user2`
    FOREIGN KEY (`receiver_id`)
    REFERENCES `pay_my_buddy_test`.`user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pay_my_buddy_test`.`user_account`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `user_account`;
CREATE TABLE IF NOT EXISTS `pay_my_buddy_test`.`user_account` (
  `user_id` INT NOT NULL,
  `balance` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`user_id`),
  INDEX `fk_user_account_user1_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_user_account_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `pay_my_buddy_test`.`user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pay_my_buddy_test`.`user_connection`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `user_connection`;
CREATE TABLE IF NOT EXISTS `pay_my_buddy_test`.`user_connection` (
  `user_1_id` INT NOT NULL,
  `user_2_id` INT NOT NULL,
  PRIMARY KEY (`user_1_id`, `user_2_id`),
  INDEX `fk_user_has_user_user2_idx` (`user_2_id` ASC) VISIBLE,
  INDEX `fk_user_has_user_user1_idx` (`user_1_id` ASC) VISIBLE,
  CONSTRAINT `fk_user_has_user_user1`
    FOREIGN KEY (`user_1_id`)
    REFERENCES `pay_my_buddy_test`.`user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_has_user_user2`
    FOREIGN KEY (`user_2_id`)
    REFERENCES `pay_my_buddy_test`.`user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- CREATING MOCK DATA FOR TESTING AND DEMO PURPOSES
-- -----------------------------------------------------

INSERT INTO `user`(user_id, user_name, email, password) VALUES(1, 'user1', 'user1@gmail.com', '$2a$10$YDEuPHvxnF0sVchoUvHLSuDFWkBPHF85q0lSjCSlO.Bwr/TWnNXJ.');
INSERT INTO `user`(user_id, user_name, email, password) VALUES(2, 'user2', 'user2@gmail.com', '$2a$10$YDEuPHvxnF0sVchoUvHLSuDFWkBPHF85q0lSjCSlO.Bwr/TWnNXJ.');
INSERT INTO `user`(user_id, user_name, email, password) VALUES(3, 'user3', 'user3@gmail.com', '$2a$10$YDEuPHvxnF0sVchoUvHLSuDFWkBPHF85q0lSjCSlO.Bwr/TWnNXJ.');

INSERT INTO `user_account`(user_id, balance) VALUES(1, 100);
INSERT INTO `user_account`(user_id, balance) VALUES(2, 100);
INSERT INTO `user_account`(user_id, balance) VALUES(3, 100);