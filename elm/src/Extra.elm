module Extra exposing (foldl, sliceMaybe)

import Array
import Matrix


foldl : (a -> b -> b) -> b -> (b -> b -> b) -> Matrix.Matrix a -> b
foldl function acc accJoin matrix =
    Array.foldl (\ma a -> Array.foldl function acc ma |> accJoin a) acc matrix



{-
   Create a new matrix by taking a slice from a matrix.
-}
{-
   slice : Int -> Int -> Int -> Int -> Matrix.Matrix a -> Matrix.Matrix a
   slice row0 col0 row1 col1 matrix =
       Array.slice row0 row1 matrix
           |> Array.map (\r -> Array.slice col0 col1 r)

-}


{-| A slice that are guaranteed the size given but with maybe content
since it can be outside the boundaries.
-}
sliceMaybe : Int -> Int -> Int -> Int -> Matrix.Matrix a -> Matrix.Matrix (Maybe a)
sliceMaybe row0 col0 row1 col1 matrix =
    List.range row0 row1
        |> List.map
            (\row ->
                List.range col0 col1
                    |> List.map (\col -> Matrix.get matrix row col)
                    |> Array.fromList
            )
        |> Array.fromList
