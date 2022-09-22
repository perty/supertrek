module Navigation exposing (suite)

import Expect exposing (Expectation)
import Game
import Test exposing (..)


suite : Test
suite =
    let
        ( model, _ ) =
            Game.init
    in
    describe
        """
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
    """
        [ test "Scenario: Crashing into a star while moving" <|
            \_ ->
                let
                    newModel =
                        model
                            |> quadrant_at 3 5
                            |> starship_is_located_at_sector 3 5
                            |> star_is_located_at_sector 3 7
                            |> issuing_command_NAV 1.0 1.0
                in
                Expect.all
                    [ \m -> Expect.true "Scenario failed" <| starship_is_moved_to_sector 3 6 m
                    , \m -> Expect.true "Scenario failed" <| there_is_a_message_containing "BAD NAVIGATION" m
                    ]
                    newModel
        ]


quadrant_at : Int -> Int -> Game.Model -> Game.Model
quadrant_at row col model =
    model


starship_is_located_at_sector : Int -> Int -> Game.Model -> Game.Model
starship_is_located_at_sector row col model =
    model


star_is_located_at_sector : Int -> Int -> Game.Model -> Game.Model
star_is_located_at_sector row col model =
    model


issuing_command_NAV : Float -> Float -> Game.Model -> Game.Model
issuing_command_NAV course warp model =
    model


starship_is_moved_to_sector : Int -> Int -> Game.Model -> Bool
starship_is_moved_to_sector row col model =
    True


there_is_a_message_containing : String -> Game.Model -> Bool
there_is_a_message_containing message model =
    True
