Feature: Supporter Account Registration

  As a new supporter,
  I want to register via the official Basketball England form,
  So that I can access membership services.

  Scenario: Successful registration with valid input
    Given I open the registration page in "chrome"
    When I fill in the form with:
      | Date of Birth      | 01/01/2000           |
      | First Name         | John                 |
      | Last Name          | Doe                  |
      | Email              | john20@test.com       |
      | Confirm Email      | john20@test.com       |
      | Password           | Password123!         |
      | Confirm Password   | Password123!         |
      | Choose Role        | Fan                  |
      | Accept Terms       | true                 |
      | Accept Age Confirm | true                 |
      | Accept Code        | true                 |
    And I submit the registration form
    Then I should see a registration success message

  Scenario: Missing last name
    Given I open the registration page in "chrome"
    When I fill in the form with:
      | Date of Birth      | 01/01/2000           |
      | First Name         | John                 |
      | Last Name          |                      |
      | Email              | john50@test.com        |
      | Confirm Email      | john50@test.com        |
      | Password           | Password123!         |
      | Confirm Password   | Password123!         |
      | Choose Role        | Fan                  |
      | Accept Terms       | true                 |
      | Accept Age Confirm | true                 |
      | Accept Code        | true                 |
    And I submit the registration form
    Then I should see an error for missing last name

  Scenario: Passwords do not match
    Given I open the registration page in "chrome"
    When I fill in the form with:
      | Date of Birth      | 01/01/2000           |
      | First Name         | John                 |
      | Last Name          | Doe                  |
      | Email              | john100@test.com        |
      | Confirm Email      | john100@test.com        |
      | Password           | Password123!         |
      | Confirm Password   | WrongPass123!        |
      | Choose Role        | Fan                  |
      | Accept Terms       | true                 |
      | Accept Age Confirm | true                 |
      | Accept Code        | true                 |
    And I submit the registration form
    Then I should see an error for password mismatch

  Scenario: Terms and conditions not accepted
    Given I open the registration page in "chrome"
    When I fill in the form with:
      | Date of Birth      | 01/01/2000           |
      | First Name         | John                 |
      | Last Name          | Doe                  |
      | Email              | john45@test.com        |
      | Confirm Email      | john45@test.com        |
      | Password           | Password123!         |
      | Confirm Password   | Password123!         |
      | Choose Role        | Fan                  |
      | Accept Terms       | false                |
      | Accept Age Confirm | true                 |
      | Accept Code        | true                 |
    And I submit the registration form
    Then I should see an error for terms not accepted

  Scenario Outline: Registration with different roles
    Given I open the registration page in "chrome"
    When I fill in the form with:
      | Date of Birth      | 01/01/2000              |
      | First Name         | Jane                    |
      | Last Name          | Doe                     |
      | Email              | jane98@test.com           |
      | Confirm Email      | jane98@test.com           |
      | Password           | Password123!            |
      | Confirm Password   | Password123!            |
      | Choose Role        | <role>                  |
      | Accept Terms       | true                    |
      | Accept Age Confirm | true                    |
      | Accept Code        | true                    |
    And I submit the registration form
    Then I should see a registration success message

    Examples:
      | role   |
      | Fan    |
      | Coach  |
      | Player |

