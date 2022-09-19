module Main exposing (Model, Msg(..), init, main)

import Array exposing (Array)
import Browser
import Browser.Events exposing (onKeyUp)
import Game
import Html exposing (Html, div, text, textarea)
import Html.Attributes exposing (cols, readonly, rows, style)
import Json.Decode as Decode


main =
    Browser.element
        { init = init
        , view = view
        , update = update
        , subscriptions = subscriptions
        }


type Msg
    = PressedLetter Char
    | Control String
    | GameMsg Game.Msg


type alias Model =
    { inputString : String
    , gameModel : Game.Model
    }


init : () -> ( Model, Cmd Msg )
init _ =
    let
        ( newModel, newCmd ) =
            Game.init
    in
    ( { inputString = ""
      , gameModel = newModel
      }
    , Cmd.map GameMsg newCmd
    )



-- Update


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        GameMsg m ->
            let
                ( newModel, newCmd ) =
                    Game.update m model.gameModel
            in
            ( { model | gameModel = newModel }
            , Cmd.map GameMsg newCmd
            )

        PressedLetter char ->
            ( { model
                | inputString =
                    killLastChar model.inputString
                        ++ String.fromChar char
                        ++ "_"
                        |> String.toUpper
              }
            , Cmd.none
            )

        Control string ->
            case string of
                "Enter" ->
                    let
                        ( newModel, newCmd ) =
                            Game.update (Game.Enter (killLastChar model.inputString)) model.gameModel
                    in
                    ( { model | inputString = "_", gameModel = newModel }
                    , Cmd.map GameMsg newCmd
                    )

                "Backspace" ->
                    ( { model
                        | inputString =
                            (model.inputString
                                |> killLastChar
                                |> killLastChar
                            )
                                ++ "_"
                      }
                    , Cmd.none
                    )

                _ ->
                    ( model, Cmd.none )


killLastChar : String -> String
killLastChar string =
    String.slice 0 (String.length string - 1) string



-- View


view : Model -> Html Msg
view model =
    div []
        [ textarea
            [ cols 80
            , rows 24
            , style "resize" "none"
            , readonly True
            , style "background-color" "black"
            , style "color" "lightgreen"
            , style "border-style" "solid"
            , style "border-radius" "15px"
            , style "font-family" "monospace"
            ]
            ((Array.map (\s -> text s) model.gameModel.terminalLines |> Array.toList) ++ [ text model.inputString ])
        ]


keyDecoder : Decode.Decoder Msg
keyDecoder =
    Decode.map toKey (Decode.field "key" Decode.string)


toKey : String -> Msg
toKey string =
    case String.uncons string of
        Just ( char, "" ) ->
            PressedLetter char

        _ ->
            Control string



-- Subscription


subscriptions : Model -> Sub Msg
subscriptions _ =
    onKeyUp keyDecoder
