@1.0
Feature: Navigation
  The navigation is when the ship moves. It is initiated with the
  command "NAV" and prompted course and warp factor.

  The course is a direction numbered from decimal 1.0 to 9.0 where
  9.0 is equal to 1.0, being full circle.

  The warp factor is the amount power to apply in that direction.
  A warp of 1.0 will certainly make the ship go to the next quadrant.
  Less than 1.0 may keep the ship within the quadrant.

  As the ship travels through the quadrant, it may run into obstacles.
  It may be a star, klingon or starbase. If that happens, the ship
  stops and message about bad navigation is displayed.

  If the ship is placed in a position next to a star base, it is
  considered docked.

  Scenario Outline: Moving within the quadrant
    Given a quadrant at 2,4
    And starship is located at sector <start row>,<start col>
    When issuing command NAV <course> <warp>
    Then starship is moved to sector <end row>,<end col>

    Examples:
      | start row | start col | course | warp | end row | end col |
      | 1         | 1         | 1.0    | 0.5  | 1       | 5       |
