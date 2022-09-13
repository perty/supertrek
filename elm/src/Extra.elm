module Extra exposing (foldl)

import Array
import Matrix


foldl : (a -> b -> b) -> b -> (b -> b -> b) -> Matrix.Matrix a -> b
foldl function acc accJoin matrix =
    Array.foldl (\ma a -> Array.foldl function acc ma |> accJoin a) acc matrix
