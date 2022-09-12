module Main exposing (Model, Msg(..), init, initialModel, main)

import Array exposing (Array)
import Browser
import Browser.Events exposing (onKeyUp)
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


type alias Model =
    { inputString : String
    , terminalLines : Array String
    }


init : () -> ( Model, Cmd Msg )
init _ =
    ( initialModel, Cmd.none )


initialModel : Model
initialModel =
    { inputString = ""
    , terminalLines = Array.initialize 23 (\n -> String.fromInt n ++ "\n") |> Array.push "23"
    }



-- Update


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        PressedLetter char ->
            ( { model | inputString = model.inputString ++ String.fromChar char |> String.toUpper }, Cmd.none )

        Control string ->
            case string of
                "Enter" ->
                    ( { model
                        | terminalLines = shiftLines model.terminalLines model.inputString
                        , inputString = ""
                      }
                    , Cmd.none
                    )

                "Backspace" ->
                    ( { model | inputString = String.slice 0 (String.length model.inputString - 1) model.inputString }, Cmd.none )

                _ ->
                    ( model, Cmd.none )


shiftLines : Array String -> String -> Array String
shiftLines array inputString =
    let
        lastLine =
            Array.get 23 array |> Maybe.withDefault "" |> String.replace "\n" ""
    in
    Array.slice 1 23 array |> Array.push (lastLine ++ inputString ++ "\n")



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
            ]
            ((Array.map (\s -> text s) model.terminalLines |> Array.toList) ++ [ text model.inputString ])
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



{-
   [ text "12345678901234567890123456789012345678901234567890123456789012345678901234567890\n"
               , text "2        1         2         3         4         5         6         7         8\n"
               , text "32345678901234567890123456789012345678901234567890123456789012345678901234567890\n"
               , text "42345678901234567890123456789012345678901234567890123456789012345678901234567890\n"
               , text "5        1         2         3         4         5         6         7         8\n"
               , text "62345678901234567890123456789012345678901234567890123456789012345678901234567890\n"
               , text "72345678901234567890123456789012345678901234567890123456789012345678901234567890\n"
               , text "8        1         2         3         4         5         6         7         8\n"
               , text "92345678901234567890123456789012345678901234567890123456789012345678901234567890\n"
               , text "02345678901234567890123456789012345678901234567890123456789012345678901234567890\n"
               , text "1        1         2         3         4         5         6         7         8\n"
               , text "22345678901234567890123456789012345678901234567890123456789012345678901234567890\n"
               , text "32345678901234567890123456789012345678901234567890123456789012345678901234567890\n"
               , text "4        1         2         3         4         5         6         7         8\n"
               , text "52345678901234567890123456789012345678901234567890123456789012345678901234567890\n"
               , text "62345678901234567890123456789012345678901234567890123456789012345678901234567890\n"
               , text "7        1         2         3         4         5         6         7         8\n"
               , text "82345678901234567890123456789012345678901234567890123456789012345678901234567890\n"
               , text "92345678901234567890123456789012345678901234567890123456789012345678901234567890\n"
               , text "0        1         2         3         4         5         6         7         8\n"
               , text "12345678901234567890123456789012345678901234567890123456789012345678901234567890\n"
               , text "22345678901234567890123456789012345678901234567890123456789012345678901234567890\n"
               , text "3        1         2         3         4         5         6         7         8\n"
               , text "42345678901234567890123456789012345678901234567890123456789012345678901234567890\n"
               ]
-}
