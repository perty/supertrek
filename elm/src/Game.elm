module Game exposing (Model, Msg(..), Position, init, update)

import Array exposing (Array)
import Extra exposing (foldl, sliceMaybe)
import Matrix exposing (Matrix)
import Random


type alias Model =
    { state : GameState
    , terminalLines : Array String
    , quadrant : Position
    , quadrantContent : QuadrantContent
    , sector : Position
    , energy : Float
    , shieldLevel : Float
    , damage : Damage
    , currentDate : Float
    , startDate : Float
    , missionDays : Float
    , torpedoes : Int
    , galaxy : Galaxy
    , galaxySetup : Int
    , setUpProgress : Int
    , route : Float
    }


type GameState
    = Initial
    | AwaitFirstEnter
    | SetUpCurrentQuadrant Int
    | AwaitCommand
    | AwaitRoute
    | AwaitWarp
    | AwaitTorpedoCourse


type Msg
    = Enter String
    | InitPosition ToPosition Position
    | InitFloat FloatField Float
    | InitQuadrant (List Float)
    | InitKlingon Cell
    | InitStar Position
    | InitBase Position


type alias Damage =
    { warpEngines : Float
    , shortRangeSensors : Float
    , longRangeSensors : Float
    , torpedoes : Float
    }


type Cell
    = KlingonCell Position Float
    | StarbaseCell
    | StarCell
    | StarshipCell
    | EmptyCell


type alias QuadrantContent =
    { content : Matrix Cell
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


type alias Galaxy =
    Matrix Quadrant


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
      , quadrantContent = initQuadrant
      , sector = Position 0 0
      , energy = initialEnergy
      , shieldLevel = 0
      , damage = Damage 0 0 0 0
      , currentDate = 0
      , startDate = 0
      , missionDays = 0
      , torpedoes = initialTorpedoes
      , galaxy = Matrix.repeat 8 8 (Quadrant -1 -1 -1)
      , galaxySetup = 0
      , setUpProgress = 0
      , route = -1
      }
    , Cmd.batch
        [ Random.generate (InitPosition QuadrantPosition) randomPosition
        , Random.generate (InitPosition SectorPosition) randomPosition
        , Random.generate (InitFloat CurrentDate) randomFloat
        , Random.generate (InitFloat MissionDays) randomFloat
        , Random.generate InitQuadrant randomQuadrant
        ]
    )


initQuadrant : QuadrantContent
initQuadrant =
    QuadrantContent (Matrix.repeat 8 8 EmptyCell)


randomFloat : Random.Generator Float
randomFloat =
    Random.float 0 1


randomPosition : Random.Generator Position
randomPosition =
    Random.map2 Position
        (Random.int 1 8)
        (Random.int 1 8)


randomKlingon : Random.Generator Cell
randomKlingon =
    Random.map3 (\row col health -> KlingonCell (Position row col) (100 + health))
        (Random.int 1 8)
        (Random.int 1 8)
        (Random.float 0 1)


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
            let
                lastLine =
                    Array.get 23 model.terminalLines |> Maybe.withDefault ""

                commandAdded =
                    { model | terminalLines = Array.set 23 (lastLine ++ command) model.terminalLines }
            in
            parseCommand commandAdded command

        InitPosition toPosition pos ->
            case toPosition of
                QuadrantPosition ->
                    ( { model | quadrant = pos }, Cmd.none )

                SectorPosition ->
                    ( { model
                        | sector = pos
                        , quadrantContent = insertIconInQuadrant model.quadrantContent pos StarshipCell
                      }
                    , Cmd.none
                    )

        InitFloat floatField value ->
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

        InitQuadrant floats ->
            let
                newGalaxy =
                    addQuadrant floats model.galaxy model.galaxySetup
            in
            if model.galaxySetup > 63 then
                ( { model
                    | galaxy = newGalaxy
                    , state = AwaitFirstEnter
                    , terminalLines = afterInitial model
                  }
                , Cmd.none
                )

            else
                ( { model | galaxy = newGalaxy, galaxySetup = model.galaxySetup + 1 }
                , Random.generate InitQuadrant randomQuadrant
                )

        InitStar pos ->
            if not <| checkForIcon model.quadrantContent pos EmptyCell then
                ( model, Random.generate InitStar randomPosition )

            else
                ( { model
                    | quadrantContent = insertIconInQuadrant model.quadrantContent pos StarCell
                  }
                    |> countDownSetUpCurrentQuadrant
                , Cmd.none
                )

        InitBase pos ->
            if not <| checkForIcon model.quadrantContent pos EmptyCell then
                ( model, Random.generate InitBase randomPosition )

            else
                ( { model
                    | quadrantContent = insertIconInQuadrant model.quadrantContent pos StarbaseCell
                  }
                    |> countDownSetUpCurrentQuadrant
                , Cmd.none
                )

        InitKlingon cell ->
            case cell of
                KlingonCell pos _ ->
                    if not <| checkForIcon model.quadrantContent pos EmptyCell then
                        ( model, Random.generate InitKlingon randomKlingon )

                    else
                        ( { model
                            | quadrantContent = insertIconInQuadrant model.quadrantContent pos cell
                          }
                            |> countDownSetUpCurrentQuadrant
                        , Cmd.none
                        )

                _ ->
                    ( model, Cmd.none )


countDownSetUpCurrentQuadrant : Model -> Model
countDownSetUpCurrentQuadrant model =
    let
        remaining =
            case model.state of
                SetUpCurrentQuadrant n ->
                    n - 1

                _ ->
                    0
    in
    if remaining > 0 then
        { model
            | state = SetUpCurrentQuadrant remaining
        }

    else
        { model
            | state = AwaitCommand
        }
            |> shortRangeSensors


addQuadrant : List Float -> Galaxy -> Int -> Galaxy
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


removeKlingonFromQuadrant : Galaxy -> Position -> Galaxy
removeKlingonFromQuadrant galaxy position =
    let
        quadrant =
            getQuadrant galaxy position

        newQuadrant =
            { quadrant | klingons = quadrant.klingons - 1 }
    in
    setQuadrant galaxy position newQuadrant


removeStarbaseFromQuadrant : Galaxy -> Position -> Galaxy
removeStarbaseFromQuadrant galaxy position =
    let
        quadrant =
            getQuadrant galaxy position

        newQuadrant =
            { quadrant | bases = quadrant.bases - 1 }
    in
    setQuadrant galaxy position newQuadrant


getQuadrant : Galaxy -> Position -> Quadrant
getQuadrant galaxy position =
    Matrix.get galaxy (position.row - 1) (position.col - 1)
        |> Maybe.withDefault (Quadrant 0 0 0)


setQuadrant : Galaxy -> Position -> Quadrant -> Galaxy
setQuadrant galaxy position newQuadrant =
    Matrix.set galaxy (position.row - 1) (position.col - 1) newQuadrant


fnr r =
    r * 8 + 1 |> Basics.round


totalKlingons : Galaxy -> Int
totalKlingons matrix =
    foldl (\q a -> q.klingons + a) 0 (+) matrix


totalBases : Galaxy -> Int
totalBases matrix =
    foldl (\q a -> q.bases + a) 0 (+) matrix


parseCommand : Model -> String -> ( Model, Cmd Msg )
parseCommand model command =
    case model.state of
        AwaitCommand ->
            case command of
                "NAV" ->
                    routePrompt model

                "SRS" ->
                    ( shortRangeSensors model, Cmd.none )

                "LRS" ->
                    ( longRangeSensors model, Cmd.none )

                "TOR" ->
                    ( photonTorpedo model, Cmd.none )

                "GRS" ->
                    ( galaxySensor model, Cmd.none )

                _ ->
                    helpCommand model

        Initial ->
            ( model, Cmd.none )

        AwaitFirstEnter ->
            newQuadrantEntered model

        SetUpCurrentQuadrant _ ->
            ( model, Cmd.none )

        AwaitRoute ->
            ( warpPrompt command model, Cmd.none )

        AwaitWarp ->
            navigate command model |> appendCommandPrompt

        AwaitTorpedoCourse ->
            ( sendTorpedo command model, Cmd.none ) |> appendCommandPrompt


routePrompt : Model -> ( Model, Cmd Msg )
routePrompt model =
    ( { model
        | state = AwaitRoute
        , terminalLines =
            model.terminalLines
                |> println ""
                |> print "COURSE (1-8): "
      }
    , Cmd.none
    )


warpPrompt : String -> Model -> Model
warpPrompt command model =
    let
        errorCase =
            { model
                | state = AwaitCommand
                , terminalLines =
                    model.terminalLines
                        |> println " LT. SULU REPORTS 'INCORRECT COURSE DATA, SIR!'"
                        |> commandPrompt
            }
    in
    case String.toFloat command of
        Nothing ->
            errorCase

        Just route ->
            if route < 1 || route > 8 then
                errorCase

            else
                { model
                    | state = AwaitWarp
                    , route = route
                    , terminalLines =
                        model.terminalLines
                            |> println ""
                            |> print ("WARP FACTOR (0 - " ++ (String.fromFloat <| maxWarp model) ++ "): ")
                }


maxWarp : Model -> Float
maxWarp model =
    if model.damage.warpEngines < 0 then
        0.2

    else
        8


navigate : String -> Model -> ( Model, Cmd Msg )
navigate command model =
    case String.toFloat command of
        Nothing ->
            ( model, Cmd.none )

        Just warp ->
            if warp <= 0 || warp > maxWarp model then
                ( { model
                    | terminalLines =
                        model.terminalLines
                            |> println ("WARP MUST BE GREATER THAN ZERO AND MAXIMUM " ++ String.fromFloat (maxWarp model))
                  }
                , Cmd.none
                )

            else
                let
                    stepsN =
                        intFloor (warp * 8 + 0.5)

                    removedShip =
                        { model
                            | quadrantContent = insertIconInQuadrant model.quadrantContent model.sector EmptyCell
                        }
                in
                removedShip
                    |> klingonsMoveAndFire
                    |> moveStarShip stepsN


appendCommandPrompt : ( Model, Cmd Msg ) -> ( Model, Cmd Msg )
appendCommandPrompt ( model, cmd ) =
    ( { model
        | state = AwaitCommand
        , terminalLines =
            model.terminalLines
                |> println ""
                |> commandPrompt
      }
    , cmd
    )


klingonsMoveAndFire : Model -> Model
klingonsMoveAndFire model =
    model


moveStarShip : Int -> Model -> ( Model, Cmd Msg )
moveStarShip stepsN model =
    if stepsN <= 0 then
        ( { model
            | state = AwaitCommand
            , quadrantContent = insertIconInQuadrant model.quadrantContent model.sector StarshipCell
          }
        , Cmd.none
        )

    else
        let
            iRoute =
                model.route |> Basics.round

            x1 =
                cFaktor iRoute 1 + (cFaktor (iRoute + 1) 1 - cFaktor iRoute 1) * (model.route - Basics.toFloat iRoute)

            x2 =
                cFaktor iRoute 2 + (cFaktor (iRoute + 1) 2 - cFaktor iRoute 2) * (model.route - Basics.toFloat iRoute)

            newSector =
                Debug.log "newSector" <|
                    Position (model.sector.row + Basics.floor x1) (model.sector.col + Basics.floor x2)
        in
        if newSector.row > 8 || newSector.col > 8 || newSector.row < 1 || newSector.col < 1 then
            exceededQuadrantLimits newSector model

        else if not <| checkForIcon model.quadrantContent newSector EmptyCell then
            ( crashedInto model, Cmd.none )

        else
            { model | sector = newSector }
                |> moveStarShip (stepsN - 1)


cFaktor : Int -> Int -> Float
cFaktor route xOrY =
    case ( route, xOrY ) of
        ( 3, 1 ) ->
            -1

        ( 2, 1 ) ->
            -1

        ( 4, 1 ) ->
            -1

        ( 4, 2 ) ->
            -1

        ( 5, 2 ) ->
            -1

        ( 6, 2 ) ->
            -1

        ( 1, 2 ) ->
            1

        ( 2, 2 ) ->
            1

        ( 6, 1 ) ->
            1

        ( 7, 1 ) ->
            1

        ( 8, 1 ) ->
            1

        ( 8, 2 ) ->
            1

        ( 9, 2 ) ->
            1

        _ ->
            0


crashedInto : Model -> Model
crashedInto model =
    { model
        | state = AwaitCommand
        , terminalLines =
            model.terminalLines
                |> println ""
                |> println "WARP ENGINES SHUT DOWN AT"
                |> println ("SECTOR " ++ posToString model.sector ++ " DUE TO BAD NAVIGATION.")
    }


exceededQuadrantLimits : Position -> Model -> ( Model, Cmd Msg )
exceededQuadrantLimits newSector model =
    let
        rowOffset =
            case newSector.row of
                0 ->
                    -1

                9 ->
                    1

                _ ->
                    0

        colOffset =
            case newSector.col of
                0 ->
                    -1

                9 ->
                    1

                _ ->
                    0

        newQuadrant =
            Position (model.quadrant.row + rowOffset) (model.quadrant.col + colOffset)
    in
    if newQuadrant.row > 8 || newQuadrant.col > 8 || newQuadrant.row < 1 || newQuadrant.col < 1 then
        exceededGalaxyLimits model

    else
        newQuadrantEntered { model | quadrant = newQuadrant }


exceededGalaxyLimits : Model -> ( Model, Cmd Msg )
exceededGalaxyLimits model =
    let
        _ =
            Debug.log "exceed galaxy" model.quadrant
    in
    ( { model
        | terminalLines =
            model.terminalLines
                |> println ""
                |> println "LT-UHURA REPORTS MESSAGE FROM STARFLEET COMMAND:"
                |> println "’ PERMISSION TO ATTEMPT CROSSING OF GALACTIC PERIMETER"
                |> println " IS HEREBY *DENIED*. SHUT DOWN YOUR ENGINES.'"
                |> println "CHIEF ENGINEER SCOTT REPORTS ‘WARP ENGINES SHUT DOWN'"
                |> println (" AT SECTOR " ++ posToString model.sector ++ " OF QUADRANT " ++ posToString model.quadrant ++ " '")
      }
    , Cmd.none
    )


newQuadrantEntered : Model -> ( Model, Cmd Msg )
newQuadrantEntered model =
    let
        currentQuadrant : Quadrant
        currentQuadrant =
            getQuadrant model.galaxy model.quadrant

        klingons =
            List.range 1 currentQuadrant.klingons
                |> List.map (\_ -> Random.generate InitKlingon randomKlingon)

        stars =
            List.range 1 currentQuadrant.stars
                |> List.map (\_ -> Random.generate InitStar randomPosition)

        bases =
            List.range 1 currentQuadrant.bases
                |> List.map (\_ -> Random.generate InitBase randomPosition)
    in
    ( { model
        | state = SetUpCurrentQuadrant (currentQuadrant.klingons + currentQuadrant.stars + currentQuadrant.bases)
        , quadrantContent = insertIconInQuadrant initQuadrant model.sector StarshipCell
        , terminalLines =
            model.terminalLines
                |> println ""
                |> println ("NOW ENTERING QUADRANT " ++ quadrantName model.quadrant)
      }
    , Cmd.batch (klingons ++ stars ++ bases)
    )


quadrantName : Position -> String
quadrantName quadrant =
    if quadrant.col <= 4 then
        case quadrant.row of
            1 ->
                "ANTARES"

            2 ->
                "RIGEL"

            3 ->
                "PROCYON"

            4 ->
                "VEGA"

            5 ->
                "CANOPUS"

            6 ->
                "ALTAIR"

            7 ->
                "SAGITTARIUS"

            8 ->
                "POLLUX"

            _ ->
                "??"

    else
        case quadrant.row of
            1 ->
                "SIRIUS"

            2 ->
                "DENEB"

            3 ->
                "CAPELLA"

            4 ->
                "BETELGEUSE"

            5 ->
                "ALDEBARAN"

            6 ->
                "REGULUS"

            7 ->
                "ARCTURUS"

            8 ->
                "SPICA"

            _ ->
                "??"


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
                    |> List.map
                        (\n ->
                            quadrantRowAsString model.quadrantContent n
                                ++ srsRight model n
                        )
        in
        { model
            | terminalLines =
                List.foldl (\str lines -> lines |> println str)
                    model.terminalLines
                    (("" :: srsDelimiter :: srsMap) ++ [ srsDelimiter ])
                    |> commandPrompt
        }


srsDelimiter =
    String.repeat 24 "-"


lrsDelimiter =
    String.repeat 19 "-"


srsRight : Model -> Int -> String
srsRight model int =
    case int of
        1 ->
            "     STARDATE           " ++ String.fromInt (intFloor ((model.currentDate * 10) * 0.1))

        2 ->
            "     CONDITION          " ++ condition model

        3 ->
            "     QUADRANT           " ++ posToString model.quadrant

        4 ->
            "     SECTOR             " ++ posToString model.sector

        5 ->
            "     PHOTON TORPEDOES   " ++ String.fromInt model.torpedoes

        6 ->
            "     TOTAL ENERGY       " ++ String.fromInt (intFloor (model.energy + model.shieldLevel))

        7 ->
            "     SHIELDS            " ++ String.fromInt (intFloor model.shieldLevel)

        8 ->
            "     KLINGONS REMAINING " ++ String.fromInt (totalKlingons model.galaxy)

        _ ->
            "?"


condition : Model -> String
condition model =
    let
        docked =
            Matrix.neighbours model.quadrantContent.content (model.sector.row - 1) (model.sector.col - 1)
                |> Array.foldl
                    (\cell baseSeen ->
                        if baseSeen then
                            True

                        else
                            cell == Just StarbaseCell
                    )
                    False

        klingons =
            foldl
                (\cell n ->
                    case cell of
                        KlingonCell _ _ ->
                            n + 1

                        _ ->
                            n
                )
                0
                (+)
                model.quadrantContent.content
    in
    if docked then
        "DOCKED"

    else if klingons > 0 then
        "RED"

    else if model.energy < initialEnergy * 0.1 then
        "YELLOW"

    else
        "GREEN"


helpCommand : Model -> ( Model, Cmd Msg )
helpCommand model =
    ( { model
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
    , Cmd.none
    )


commandPrompt strings =
    strings |> print "COMMAND "


longRangeSensors : Model -> Model
longRangeSensors model =
    if model.damage.longRangeSensors < 0 then
        { model
            | terminalLines =
                model.terminalLines
                    |> println "LONG RANGE SENSORS ARE INOPERABLE"
                    |> commandPrompt
        }

    else
        let
            headLine =
                "LONG RANGE SCAN FOR QUADDRANT " ++ posToString model.quadrant

            sliced : Matrix (Maybe Quadrant)
            sliced =
                sliceMaybe (model.quadrant.row - 2) (model.quadrant.col - 2) model.quadrant.row model.quadrant.col model.galaxy

            neighbours : List String
            neighbours =
                Matrix.map maybeQuadrantToString sliced
                    |> Array.toList
                    |> List.map (Array.toList >> String.join " : ")
                    |> List.map (\s -> ": " ++ s ++ " :")
        in
        { model
            | terminalLines =
                List.foldl (\str lines -> lines |> println str)
                    model.terminalLines
                    (([ "", headLine, lrsDelimiter ] ++ neighbours) ++ [ lrsDelimiter ])
                    |> commandPrompt
        }


photonTorpedo : Model -> Model
photonTorpedo model =
    if model.torpedoes <= 0 then
        { model
            | terminalLines =
                model.terminalLines
                    |> println ""
                    |> println "ALL PHOTON TORPEDOES EXPENDED"
        }

    else if model.damage.torpedoes < 0 then
        { model
            | terminalLines =
                model.terminalLines
                    |> println ""
                    |> println "PHOTON TUBES ARE NOT OPERATIONAL"
        }

    else
        { model
            | state = AwaitTorpedoCourse
            , terminalLines =
                model.terminalLines
                    |> println ""
                    |> print "PHOTON TORPEDO COURSE (1-9): "
        }


sendTorpedo : String -> Model -> Model
sendTorpedo command model =
    case String.toFloat command of
        Nothing ->
            model

        Just course ->
            if course < 1 || course > 9 then
                { model
                    | state = AwaitCommand
                    , terminalLines =
                        model.terminalLines
                            |> println ""
                            |> println "ENSIGN CHEKOV REPORTS, 'INCORRECT COURSE DATA, SIR!'"
                }

            else
                sendTorpedoHelper model.sector
                    course
                    { model
                        | torpedoes = model.torpedoes - 1
                        , terminalLines =
                            model.terminalLines
                                |> println ""
                                |> println "TORPEDO COURSE"
                    }


sendTorpedoHelper : Position -> Float -> Model -> Model
sendTorpedoHelper pos course model =
    let
        iroute =
            course |> Basics.round

        x1 =
            cFaktor iroute 1 + (cFaktor (iroute + 1) 1 - cFaktor iroute 1) * (course - Basics.toFloat iroute)

        x2 =
            cFaktor iroute 2 + (cFaktor (iroute + 1) 2 - cFaktor iroute 2) * (course - Basics.toFloat iroute)

        newPos =
            Position (pos.row + Basics.floor x1) (pos.col + Basics.floor x2)
    in
    if pos.row < 1 || pos.row > 8 || pos.col < 1 || pos.col > 8 then
        { model
            | terminalLines =
                model.terminalLines
                    |> println ""
                    |> println "TORPEDO MISSED"
                    |> commandPrompt
        }

    else
        case getIconInQuadrant model.quadrantContent newPos of
            EmptyCell ->
                sendTorpedoHelper newPos
                    course
                    { model
                        | terminalLines =
                            model.terminalLines
                                |> println (posToString newPos)
                    }

            KlingonCell _ _ ->
                { model
                    | terminalLines =
                        model.terminalLines
                            |> println "*** KLINGON DESTROYED ***"
                    , quadrantContent = insertIconInQuadrant model.quadrantContent newPos EmptyCell
                    , galaxy = removeKlingonFromQuadrant model.galaxy model.quadrant
                }

            StarbaseCell ->
                let
                    newModel =
                        { model
                            | terminalLines =
                                model.terminalLines
                                    |> println "*** STARBASE DESTROYED ***"
                            , quadrantContent = insertIconInQuadrant model.quadrantContent newPos EmptyCell
                            , galaxy = removeStarbaseFromQuadrant model.galaxy model.quadrant
                        }
                in
                if totalBases newModel.galaxy <= 0 then
                    { newModel
                        | terminalLines =
                            newModel.terminalLines
                                |> println "THAT DOES IT, CAPTAIN!! YOU ARE HEREBY RELIEVED OF COMMAND"
                                |> println "AND SENTENCED TO 99 STARDATES AT HARD LABOR ON CYGNUS 12!!"
                    }
                        |> gotoXXX6270

                else
                    { newModel
                        | terminalLines =
                            newModel.terminalLines
                                |> println "STARFLEET COMMAND REVIEWING YOUR RECORD TO CONSIDER"
                                |> println "COURT MARTIAL!"
                    }

            StarCell ->
                { model
                    | terminalLines =
                        model.terminalLines
                            |> println ("STAR AT " ++ posToString newPos ++ " ABSORBED TORPEDO ENERGY.")
                }
                    |> klingonsMoveAndFire

            StarshipCell ->
                model


gotoXXX6270 : Model -> Model
gotoXXX6270 model =
    model


galaxySensor : Model -> Model
galaxySensor model =
    let
        headLine =
            List.range 1 8 |> List.map paddedInt |> String.join ""

        galaxyStrings : List String
        galaxyStrings =
            foldl (\quadrant acc -> acc ++ " " ++ quadrantToString quadrant) "" (\s1 s2 -> s1 ++ "|" ++ s2) model.galaxy
                |> String.split "|"
                |> List.tail
                |> Maybe.withDefault []
                |> List.indexedMap (\n s -> String.fromInt (n + 1) ++ s)
    in
    { model
        | terminalLines =
            List.foldl (\str lines -> lines |> println str)
                model.terminalLines
                (([ "", headLine ] ++ galaxyStrings) ++ [])
                |> commandPrompt
    }


quadrantToString : Quadrant -> String
quadrantToString quadrant =
    String.fromInt (quadrant.klingons * 100 + quadrant.bases * 10 + quadrant.stars) |> String.padLeft 3 '0'


maybeQuadrantToString : Maybe Quadrant -> String
maybeQuadrantToString maybeQuadrant =
    case maybeQuadrant of
        Just q ->
            quadrantToString q

        Nothing ->
            "***"


paddedInt : Int -> String
paddedInt int =
    int
        |> String.fromInt
        |> String.padLeft 4 ' '


insertIconInQuadrant : QuadrantContent -> Position -> Cell -> QuadrantContent
insertIconInQuadrant content pos icon =
    { content
        | content = Matrix.set content.content (pos.row - 1) (pos.col - 1) icon
    }


checkForIcon : QuadrantContent -> Position -> Cell -> Bool
checkForIcon quadrantContent pos cell =
    case Matrix.get quadrantContent.content (pos.row - 1) (pos.col - 1) of
        Just c ->
            c == cell

        Nothing ->
            False


getIconInQuadrant : QuadrantContent -> Position -> Cell
getIconInQuadrant quadrantContent pos =
    case Matrix.get quadrantContent.content (pos.row - 1) (pos.col - 1) of
        Just c ->
            c

        Nothing ->
            EmptyCell


quadrantRowAsString : QuadrantContent -> Int -> String
quadrantRowAsString quadrantContent row =
    Matrix.getXs quadrantContent.content (row - 1)
        |> Array.map cellToString
        |> Array.toList
        |> String.join ""


cellToString : Cell -> String
cellToString cell =
    case cell of
        EmptyCell ->
            "   "

        KlingonCell _ _ ->
            "+K+"

        StarbaseCell ->
            "<!>"

        StarCell ->
            " * "

        StarshipCell ->
            "<*>"


posToString : Position -> String
posToString position =
    String.fromInt position.row ++ "," ++ String.fromInt position.col


intFloor f =
    Basics.floor f
