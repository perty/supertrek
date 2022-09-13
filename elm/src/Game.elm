module Game exposing (Model, Msg(..), Position, init, update)

import Array exposing (Array)
import Matrix exposing (Matrix)
import Random


type alias Model =
    { state : GameState
    , terminalLines : Array String
    , quadrant : Position
    , sector : Position
    , energy : Float
    , shieldLevel : Float
    , damage : Damage
    , currentDate : Float
    , startDate : Float
    , missionDays : Float
    , torpedoes : Int
    , galaxy : Matrix Quadrant
    , galaxySetup : Int
    , setUpProgress : Int
    }


type GameState
    = Initial
    | AwaitCommand


type Msg
    = Enter String
    | SetPosition ToPosition ( Int, Int )
    | SetFloat FloatField Float
    | SetQuadrant (List Float)


type alias Damage =
    { shortRangeSensors : Float
    }


type ToPosition
    = QuadrantPosition
    | SectorPosition


type FloatField
    = CurrentDate
    | MissionDays


type alias Position =
    { row : Int
    , col : Int
    }


type alias Quadrant =
    { klingons : Int
    , bases : Int
    , stars : Int
    }


init : ( Model, Cmd Msg )
init =
    ( { state = Initial
      , terminalLines = initialScreen
      , quadrant = Position 0 0
      , sector = Position 0 0
      , energy = initialEnergy
      , shieldLevel = 0
      , damage = Damage 0
      , currentDate = 0
      , startDate = 0
      , missionDays = 0
      , torpedoes = initialTorpedoes
      , galaxy = Matrix.repeat 8 8 (Quadrant -1 -1 -1)
      , galaxySetup = 0
      , setUpProgress = 0
      }
    , Cmd.batch
        [ Random.generate (SetPosition QuadrantPosition) randomPosition
        , Random.generate (SetPosition SectorPosition) randomPosition
        , Random.generate (SetFloat CurrentDate) randomFloat
        , Random.generate (SetFloat MissionDays) randomFloat
        , Random.generate SetQuadrant randomQuadrant
        ]
    )


randomFloat : Random.Generator Float
randomFloat =
    Random.float 0 1


randomPosition : Random.Generator ( Int, Int )
randomPosition =
    Random.pair (Random.int 1 8) (Random.int 1 8)


randomQuadrant : Random.Generator (List Float)
randomQuadrant =
    Random.list 3 (Random.float 0 1)


initialEnergy =
    3000


initialTorpedoes =
    10


initialScreen : Array String
initialScreen =
    Array.empty
        |> println "                                  ,-----*-----,"
        |> println "                 ,--------------  `----  ----'"
        |> println "                  '--------- --'      / /"
        |> println "                       ,---' '-------/ /--,"
        |> println "                        '----------------'"
        |> println ""
        |> println "                 THE USS ENTERPRISE --~- NCC~1701"


afterInitial : Model -> Array String
afterInitial model =
    model.terminalLines
        |> println "YOUR ORDERS ARE AS FOLLOWS:"
        |> println ("  DESTROY THE " ++ String.fromInt (totalKlingons model.galaxy) ++ " KLINGON WARSHIPS WHICH HAS INVADED")
        |> println "  THE GALAXY BEFORE THEY CAN ATTACK FEDERATION HEADQUARTERS"
        |> println ("  ON STARDATE " ++ String.fromFloat (model.startDate + model.missionDays) ++ ". THIS GIVES YOU " ++ String.fromFloat model.missionDays ++ " DAYS. THERE ARE")
        |> println ("  " ++ String.fromInt (totalBases model.galaxy) ++ " STARBASES" ++ " IN THE GALAXY TO RESUPPLY YOUR SHIP.")
        |> println ""
        |> println "HIT RETURN WHEN YOU ARE READY."


println : String -> Array String -> Array String
println string array =
    if Array.length array < 24 then
        Array.push (string ++ String.fromChar '\n') array

    else
        Array.slice 1 24 array |> Array.push (string ++ String.fromChar '\n')


print : String -> Array String -> Array String
print string array =
    if Array.length array < 24 then
        Array.push string array

    else
        Array.slice 1 24 array |> Array.push string


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        Enter command ->
            ( parseCommand model command, Cmd.none )

        SetPosition toPosition ( row, col ) ->
            case toPosition of
                QuadrantPosition ->
                    ( { model | quadrant = Position row col }, Cmd.none )

                SectorPosition ->
                    ( { model | sector = Position row col }, Cmd.none )

        SetFloat floatField value ->
            case floatField of
                CurrentDate ->
                    let
                        date =
                            100.0 * Basics.toFloat (Basics.floor (value * 20 + 20))
                    in
                    ( { model | currentDate = date, startDate = date }, Cmd.none )

                MissionDays ->
                    let
                        date =
                            25 + Basics.toFloat (Basics.floor (value * 10))
                    in
                    ( { model | missionDays = date }, Cmd.none )

        SetQuadrant floats ->
            let
                newGalaxy =
                    addQuadrant floats model.galaxy model.galaxySetup
            in
            if model.galaxySetup > 63 then
                ( { model
                    | galaxy = newGalaxy
                    , state = AwaitCommand
                    , terminalLines = afterInitial model
                  }
                , Cmd.none
                )

            else
                ( { model | galaxy = newGalaxy, galaxySetup = model.galaxySetup + 1 }
                , Random.generate SetQuadrant randomQuadrant
                )


addQuadrant : List Float -> Matrix Quadrant -> Int -> Matrix Quadrant
addQuadrant floats matrix galaxySetup =
    let
        array =
            Array.fromList floats

        r1 =
            Array.get 0 array |> Maybe.withDefault 0

        k3 =
            if r1 > 0.98 then
                3

            else if r1 > 0.95 then
                2

            else if r1 > 0.8 then
                1

            else
                0

        r2 =
            Array.get 1 array |> Maybe.withDefault 0

        b3 =
            if r2 > 0.96 then
                1

            else
                0

        r3 =
            Array.get 2 array |> Maybe.withDefault 0

        s3 =
            fnr r3

        q =
            Quadrant k3 b3 s3

        setX =
            galaxySetup // 8

        setY =
            Basics.modBy 8 galaxySetup
    in
    Matrix.set matrix setX setY q


fnr r =
    r * 8 + 1 |> Basics.round


totalKlingons : Matrix Quadrant -> Int
totalKlingons matrix =
    foldl (\q a -> q.klingons + a) 0 (+) matrix


totalBases : Matrix Quadrant -> Int
totalBases matrix =
    foldl (\q a -> q.bases + a) 0 (+) matrix


parseCommand : Model -> String -> Model
parseCommand model command =
    case command of
        "SRS" ->
            shortRangeSensors model

        _ ->
            helpCommand model


shortRangeSensors : Model -> Model
shortRangeSensors model =
    if model.damage.shortRangeSensors < 0 then
        { model
            | terminalLines =
                model.terminalLines
                    |> println ""
                    |> println "*** SHORT RANGE SENSORS ARE OUT ***"
                    |> println ""
                    |> commandPrompt
        }

    else
        let
            srsMap =
                List.range 1 8
                    |> List.map String.fromInt
        in
        { model
            | terminalLines =
                List.foldl (\str lines -> lines |> println str)
                    model.terminalLines
                    ("..." :: srsMap)
                    |> commandPrompt
        }


helpCommand : Model -> Model
helpCommand model =
    { model
        | terminalLines =
            model.terminalLines
                |> println "ENTER ONE OF THE FOLLOWING:"
                |> println "  NAV (TO SET COURSE)"
                |> println "  SRS (FOR SHORT RANGE SENSOR SCAN)"
                |> println "  LRS (FOR LONG RANGE SENSOR SCAN)"
                |> println "  PHA (TO FIRE PHASERS)"
                |> println "  TOR (TO FIRE PHOTON TORPEDOES)"
                |> println "  SHE (TO RAISE OR LOWER SHIELDS)"
                |> println "  DAM (FOR DAMAGE CONTROL REPORT)"
                |> println "  COM (TO CALL ON LIBRARY-COMPUTER)"
                |> println "  XXX (TO RESIGN YOUR COMMAND)"
                |> println ""
                |> commandPrompt
    }


commandPrompt strings =
    strings |> print "COMMAND "


foldl : (a -> b -> b) -> b -> (b -> b -> b) -> Matrix.Matrix a -> b
foldl function acc accJoin matrix =
    Array.foldl (\ma a -> Array.foldl function acc ma |> accJoin a) acc matrix
