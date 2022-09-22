module NavigationSpec exposing (main)

import Json.Encode as Encode
import Main as App
import Runner
import Spec exposing (Spec, describe, expect, it)
import Spec.Claim as Claim exposing (Claim)
import Spec.Markup as Markup
import Spec.Markup.Event as Event
import Spec.Markup.Selector exposing (by, id)
import Spec.Observer as Observer
import Spec.Setup as Setup
import Spec.Step as Step


main =
    Runner.browserProgram
        [ navigation ]


navigation : Spec App.Model App.Msg
navigation =
    describe "Feature: Navigation"
        [ Spec.scenario "the awesome path"
            (Spec.given
                (Setup.init (App.init ())
                    |> Setup.withView App.view
                    |> Setup.withUpdate App.update
                )
                |> Spec.when "issuing command NAV 1 1"
                    [ Markup.target << by [ id "terminal" ]
                    , key "Enter"
                    , key "N"
                    , key "A"
                    , key "V"
                    , key "Enter"
                    , key "1"
                    , key "Enter"
                    , key "1"
                    , key "Enter"
                    ]
                |> it "starship is moved to sector 3,6"
                    (Observer.observeModel sectorRow |> expect (equals 5))
            )
        ]


equals : a -> Claim a
equals =
    Claim.isEqual Debug.toString


sectorRow model =
    model.gameModel.sector.row


key : String -> Step.Context model -> Step.Command msg
key string =
    Encode.object [ ( "key", Encode.string string ) ]
        |> Event.trigger "keyup"
