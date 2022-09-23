module NavigationSpec exposing (main)

import Array
import Game
import Main as App
import Runner
import Spec exposing (Spec, describe, expect, it)
import Spec.Claim as Claim exposing (Claim)
import Spec.Command as Command
import Spec.Observer as Observer
import Spec.Setup as Setup exposing (Setup)
import Spec.Step as Step


main =
    Runner.browserProgram
        [ navigation ]


navigation : Spec App.Model App.Msg
navigation =
    describe "Feature: Navigation"
        ([ Spec.scenario "Crashing into a star while moving"
            (Spec.given
                (Setup.init (App.init ())
                    |> Setup.withView App.view
                    |> Setup.withUpdate App.update
                )
                |> Spec.when "a quadrant at 3,5"
                    [ send (Game.Enter "")
                    , send (Game.ClearQuadrant (Game.Position 3 5))
                    ]
                |> Spec.when "starship is located at sector 3,5"
                    [ send (Game.InitSectorPosition (Game.Position 3 5)) ]
                |> Spec.when "a star is located at sector 3,7"
                    [ send (Game.InitStar (Game.Position 3 7)) ]
                |> Spec.when "issuing command NAV 1 1"
                    [ send (Game.Enter "NAV")
                    , send (Game.Enter "1")
                    , send (Game.Enter "1")
                    ]
                |> it "starship is moved to sector 3,6 And there is a message containing \"BAD NAVIGATION\""
                    (Observer.observeModel identity
                        |> expect
                            (Claim.satisfying
                                [ \m -> equals 3 m.gameModel.sector.row
                                , \m -> equals 6 m.gameModel.sector.col
                                , \m -> Claim.isStringContaining 1 "BAD NAVIGATION" (lastLine m.gameModel.terminalLines)
                                ]
                            )
                    )
            )
         ]
            ++ List.map outline examples
        )


outline { startRow, startCol, course, warp, endRow, endCol } =
    Spec.scenario "Moving within the quadrant"
        (Spec.given
            (Setup.init (App.init ())
                |> Setup.withView App.view
                |> Setup.withUpdate App.update
            )
            |> Spec.when "a quadrant at 2,4"
                [ send (Game.Enter "")
                , send (Game.ClearQuadrant (Game.Position 2 4))
                ]
            |> Spec.when "starship is located at sector <start row>,<start col>"
                [ send (Game.InitSectorPosition (Game.Position startRow startCol)) ]
            |> Spec.when "issuing command NAV <course> <warp>"
                [ send (Game.Enter "NAV")
                , send (Game.Enter course)
                , send (Game.Enter warp)
                ]
            |> it "starship is moved to sector <end row>,<end col>"
                (Observer.observeModel identity
                    |> expect
                        (Claim.satisfying
                            [ \m -> equals endRow m.gameModel.sector.row
                            , \m -> equals endCol m.gameModel.sector.col
                            ]
                        )
                )
        )


type alias Example =
    { startRow : Int
    , startCol : Int
    , course : String
    , warp : String
    , endRow : Int
    , endCol : Int
    }


examples =
    [ Example 1 1 "1.0" "0.5" 1 5
    , Example 8 1 "2.0" "0.5" 4 5
    , Example 8 1 "3.0" "0.5" 4 1
    , Example 8 8 "4.0" "0.5" 4 4
    , Example 8 8 "5.0" "0.5" 8 4
    , Example 1 8 "6.0" "0.5" 5 4
    , Example 1 8 "7.0" "0.5" 5 8
    , Example 1 1 "8.0" "0.5" 5 5
    ]


equals : a -> Claim a
equals =
    Claim.isEqual Debug.toString


send : Game.Msg -> Step.Context model -> Step.Command App.Msg
send msg =
    Command.send <| Command.fake <| App.GameMsg msg


lastLine : Array.Array String -> String
lastLine array =
    Array.get 23 array |> Maybe.withDefault ""
