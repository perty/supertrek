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
        [ Spec.scenario "Crashing into a star while moving"
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


equals : a -> Claim a
equals =
    Claim.isEqual Debug.toString


send : Game.Msg -> Step.Context model -> Step.Command App.Msg
send msg =
    Command.send <| Command.fake <| App.GameMsg msg


lastLine : Array.Array String -> String
lastLine array =
    Array.get 23 array |> Maybe.withDefault ""
