
Drop procedure IF EXISTS addOfficeId;
DELIMITER //
create procedure addOfficeId() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'office_id'
     and TABLE_NAME = 'b_payments'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_payments` 
ADD COLUMN `office_id` BIGINT(20) NULL DEFAULT NULL AFTER `otp`;

END IF;
END //
DELIMITER ;
call addOfficeId();
Drop procedure IF EXISTS addOfficeId;

INSERT IGNORE INTO m_permission VALUES (null, 'Master/Others', 'CREATE_Commission_amount', 'CREATE', 'Commission_amount', '0');

INSERT IGNORE INTO m_permission VALUES (null, 'Master/Others', 'DELETE_Commission_amount', 'DELETE', 'Commission_amount', '0');

INSERT IGNORE INTO m_permission VALUES (null, 'Master/Others', 'UPDATE_Basic Validation', 'UPDATE', 'Basic Validation', '0');

INSERT IGNORE INTO m_permission VALUES (null, 'Master/Others', 'DELETE_Basic Validation', 'DELETE', 'Basic Validation', '0');

INSERT IGNORE INTO m_permission VALUES (null, 'Master/Others', 'CREATE_Basic Validation', 'CREATE', 'Basic Validation', '0');

USE `obstenant-default`;
CREATE 
     OR REPLACE ALGORITHM = UNDEFINED 
    DEFINER = `root`@`localhost` 
    SQL SECURITY DEFINER
VIEW `fin_trans_vw` AS
    SELECT DISTINCT
        `b_bill_item`.`id` AS `transId`,
        `m_appuser`.`username` AS `username`,
        `b_bill_item`.`client_id` AS `client_id`,
        NULL AS `clientName`,
        NULL AS `rate`,
        NULL AS `office_name`,
        NULL AS `paymentStatus`,
        NULL AS `note`,
        NULL AS `otp`,
        NULL AS `office_id`,
        IF((`b_charge`.`charge_type` = 'NRC'),
            'Once',
            'Periodic') AS `tran_type`,
        CAST(`b_bill_item`.`invoice_date` AS DATE) AS `transDate`,
        'INVOICE' AS `transType`,
        IF((`b_bill_item`.`invoice_amount` > 0),
            `b_bill_item`.`invoice_amount`,
            0) AS `dr_amt`,
        IF((`b_bill_item`.`invoice_amount` < 0),
            ABS(`b_bill_item`.`invoice_amount`),
            0) AS `cr_amt`,
        1 AS `flag`
    FROM
        ((`b_bill_item`
        JOIN `m_appuser`)
        JOIN `b_charge`)
    WHERE
        ((`b_bill_item`.`createdby_id` = `m_appuser`.`id`)
            AND (`b_bill_item`.`id` = `b_charge`.`billitem_id`)
            AND (`b_bill_item`.`invoice_date` <= NOW())) 
    UNION ALL SELECT DISTINCT
        `b_adjustments`.`id` AS `transId`,
        `m_appuser`.`username` AS `username`,
        `b_adjustments`.`client_id` AS `client_id`,
        NULL AS `clientName`,
        NULL AS `rate`,
        NULL AS `office_name`,
        NULL AS `paymentStatus`,
        NULL AS `note`,
        NULL AS `otp`,
        NULL AS `office_id`,
        (SELECT 
                `m_code_value`.`code_value`
            FROM
                `m_code_value`
            WHERE
                ((`m_code_value`.`code_id` = 12)
                    AND (`b_adjustments`.`adjustment_code` = `m_code_value`.`id`))) AS `tran_type`,
        CAST(`b_adjustments`.`adjustment_date` AS DATE) AS `transdate`,
        'ADJUSTMENT' AS `transType`,
        0 AS `dr_amt`,
        (CASE `b_adjustments`.`adjustment_type`
            WHEN 'CREDIT' THEN `b_adjustments`.`adjustment_amount`
        END) AS `cr_amount`,
        1 AS `flag`
    FROM
        (`b_adjustments`
        JOIN `m_appuser`)
    WHERE
        ((`b_adjustments`.`adjustment_date` <= NOW())
            AND (`b_adjustments`.`adjustment_type` = 'CREDIT')
            AND (`b_adjustments`.`createdby_id` = `m_appuser`.`id`)) 
    UNION ALL SELECT DISTINCT
        `b_adjustments`.`id` AS `transId`,
        `m_appuser`.`username` AS `username`,
        `b_adjustments`.`client_id` AS `client_id`,
        NULL AS `clientName`,
        NULL AS `rate`,
        NULL AS `office_name`,
        NULL AS `paymentStatus`,
        NULL AS `note`,
        NULL AS `otp`,
        NULL AS `office_id`,
        (SELECT 
                `m_code_value`.`code_value`
            FROM
                `m_code_value`
            WHERE
                ((`m_code_value`.`code_id` = 12)
                    AND (`b_adjustments`.`adjustment_code` = `m_code_value`.`id`))) AS `tran_type`,
        CAST(`b_adjustments`.`adjustment_date` AS DATE) AS `transdate`,
        'ADJUSTMENT' AS `transType`,
        (CASE `b_adjustments`.`adjustment_type`
            WHEN 'DEBIT' THEN `b_adjustments`.`adjustment_amount`
        END) AS `dr_amount`,
        0 AS `cr_amt`,
        1 AS `flag`
    FROM
        (`b_adjustments`
        JOIN `m_appuser`)
    WHERE
        ((`b_adjustments`.`adjustment_date` <= NOW())
            AND (`b_adjustments`.`adjustment_type` = 'DEBIT')
            AND (`b_adjustments`.`createdby_id` = `m_appuser`.`id`)) 
    UNION ALL SELECT DISTINCT
        `b_payments`.`id` AS `transId`,
        `m_appuser`.`username` AS `username`,
        `b_payments`.`client_id` AS `client_id`,
        (SELECT 
                `m_client`.`display_name`
            FROM
                `m_client`
            WHERE
                (`m_client`.`id` = `b_payments`.`client_id`)) AS `clientName`,
        NULL AS `rate`,
        (SELECT 
                `m_office`.`name`
            FROM
                (`m_office`
                JOIN `m_client`)
            WHERE
                ((`m_client`.`id` = `b_payments`.`client_id`)
                    AND (`m_office`.`id` = `m_client`.`office_id`))) AS `office_name`,
        `b_payments`.`status` AS `paymentStatus`,
        `b_payments`.`Remarks` AS `note`,
        `b_payments`.`otp` AS `otp`,
        `b_payments`.`office_id` AS `office_id`,
        (SELECT 
                `m_code_value`.`code_value`
            FROM
                `m_code_value`
            WHERE
                ((`m_code_value`.`code_id` = 11)
                    AND (`b_payments`.`paymode_id` = `m_code_value`.`id`))) AS `tran_type`,
        CAST(`b_payments`.`payment_date` AS DATE) AS `transDate`,
        'PAYMENT' AS `transType`,
        0 AS `dr_amt`,
        `b_payments`.`amount_paid` AS `cr_amount`,
        `b_payments`.`is_deleted` AS `flag`
    FROM
        (`b_payments`
        JOIN `m_appuser`)
    WHERE
        ((`b_payments`.`createdby_id` = `m_appuser`.`id`)
            AND ISNULL(`b_payments`.`ref_id`)
            AND (`b_payments`.`payment_date` <= NOW())) 
    UNION ALL SELECT DISTINCT
        `b_payments`.`id` AS `transId`,
        `m_appuser`.`username` AS `username`,
        `b_payments`.`client_id` AS `client_id`,
        (SELECT 
                `m_client`.`display_name`
            FROM
                `m_client`
            WHERE
                (`m_client`.`id` = `b_payments`.`client_id`)) AS `clientName`,
        NULL AS `rate`,
        (SELECT 
                `m_office`.`name`
            FROM
                (`m_office`
                JOIN `m_client`)
            WHERE
                ((`m_client`.`id` = `b_payments`.`client_id`)
                    AND (`m_office`.`id` = `m_client`.`office_id`))) AS `office_name`,
        NULL AS `paymentStatus`,
        NULL AS `note`,
        NULL AS `otp`,
        `b_payments`.`office_id` AS `office_id`,
        (SELECT 
                `m_code_value`.`code_value`
            FROM
                `m_code_value`
            WHERE
                ((`m_code_value`.`code_id` = 11)
                    AND (`b_payments`.`paymode_id` = `m_code_value`.`id`))) AS `tran_type`,
        CAST(`b_payments`.`payment_date` AS DATE) AS `transDate`,
        'PAYMENT CANCELED' AS `transType`,
        ABS(`b_payments`.`amount_paid`) AS `dr_amt`,
        0 AS `cr_amount`,
        `b_payments`.`is_deleted` AS `flag`
    FROM
        (`b_payments`
        JOIN `m_appuser`)
    WHERE
        ((`b_payments`.`is_deleted` = 1)
            AND (`b_payments`.`ref_id` IS NOT NULL)
            AND (`b_payments`.`createdby_id` = `m_appuser`.`id`)
            AND (`b_payments`.`payment_date` <= NOW())) 
    UNION ALL SELECT DISTINCT
        `m_commission_payment`.`id` AS `transId`,
        `m_appuser`.`username` AS `username`,
        `m_commission_payment`.`client_id` AS `client_id`,
        `m_commission_payment`.`client_name` AS `clientName`,
        `m_commission_payment`.`rate` AS `rate`,
        (SELECT 
                `m_office`.`name`
            FROM
                `m_office`
            WHERE
                (`m_office`.`id` = `m_commission_payment`.`office_id`)) AS `office_name`,
        NULL AS `paymentStatus`,
        `m_commission_payment`.`Remarks` AS `note`,
        NULL AS `otp`,
        `m_commission_payment`.`office_id` AS `office_id`,
        (SELECT 
                `m_code_value`.`code_value`
            FROM
                `m_code_value`
            WHERE
                ((`m_code_value`.`code_id` = 11)
                    AND (`m_commission_payment`.`paymode_id` = `m_code_value`.`id`))) AS `tran_type`,
        CAST(`m_commission_payment`.`payment_date` AS DATE) AS `transDate`,
        'COMMISSION' AS `transType`,
        `m_commission_payment`.`debit_amount` AS `dr_amt`,
        `m_commission_payment`.`credit_amount` AS `cr_amount`,
        `m_commission_payment`.`is_deleted` AS `flag`
    FROM
        (`m_commission_payment`
        JOIN `m_appuser`)
    WHERE
        ((`m_commission_payment`.`createdby_id` = `m_appuser`.`id`)
            AND (`m_commission_payment`.`payment_date` <= NOW())) 
    UNION ALL SELECT DISTINCT
        `bjt`.`id` AS `transId`,
        `ma`.`username` AS `username`,
        `bjt`.`client_id` AS `client_id`,
        NULL AS `clientName`,
        NULL AS `rate`,
        NULL AS `office_name`,
        NULL AS `paymentStatus`,
        NULL AS `note`,
        NULL AS `otp`,
        NULL AS `office_id`,
        'Event Journal' AS `tran_type`,
        CAST(`bjt`.`jv_date` AS DATE) AS `transDate`,
        'JOURNAL VOUCHER' AS `transType`,
        IFNULL(`bjt`.`debit_amount`, 0) AS `dr_amt`,
        IFNULL(`bjt`.`credit_amount`, 0) AS `cr_amount`,
        1 AS `flag`
    FROM
        (`b_jv_transactions` `bjt`
        JOIN `m_appuser` `ma` ON (((`bjt`.`createdby_id` = `ma`.`id`)
            AND (`bjt`.`jv_date` <= NOW())))) 
    UNION ALL SELECT DISTINCT
        `bdr`.`id` AS `transId`,
        `m_appuser`.`username` AS `username`,
        `bdr`.`client_id` AS `client_id`,
        NULL AS `clientName`,
        NULL AS `rate`,
        NULL AS `office_name`,
        NULL AS `paymentStatus`,
        NULL AS `note`,
        NULL AS `otp`,
        NULL AS `office_id`,
        `bdr`.`description` AS `tran_type`,
        CAST(`bdr`.`transaction_date` AS DATE) AS `transDate`,
        'DEPOSIT&REFUND' AS `transType`,
        IFNULL(`bdr`.`debit_amount`, 0) AS `dr_amt`,
        IFNULL(`bdr`.`credit_amount`, 0) AS `cr_amount`,
        `bdr`.`is_refund` AS `flag`
    FROM
        (`b_deposit_refund` `bdr`
        JOIN `m_appuser`)
    WHERE
        ((`bdr`.`createdby_id` = `m_appuser`.`id`)
            AND (`bdr`.`transaction_date` <= NOW()))
    ORDER BY 1 , 2;


