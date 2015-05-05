import java.util.ArrayList;

public class SchoolSolver{

    public static int [][] adjacencyTable;
    public static boolean [][] unavailable;

    public static void main( String [] args ){

        if( args.length != 2 ){
            throw new IllegalArgumentException( "The inputs must be the number of rows and the number of children per row." );
        }

        int rows = Integer.parseInt( args[ 0 ] );
        int kidsPerRow = Integer.parseInt( args[ 1 ] );
        if( rows < 1 || kidsPerRow < 1 ){
            throw new IllegalArgumentException( "The inputs must be natural numbers." );
        }

        if( ( rows * kidsPerRow - 1 ) % ( kidsPerRow - 1 ) != 0 ){
            System.out.println( "Impossible!" );
            return;
        }
        int numOfChildren = rows * kidsPerRow;
        int numOfDays = ( numOfChildren - 1 )/( kidsPerRow - 1 );
        //if( numOfChildren > 30 ){
          //  throw new IllegalArgumentException( "The number of school children can be at most 30." );
        //}

        adjacencyTable = new int[ numOfChildren ][ numOfChildren ];
        for( int i = 0; i < numOfChildren; i++ ){
            adjacencyTable[ i ][ i ] = 1;
        }
        int [][][] children = new int [ rows ][ numOfDays ][ kidsPerRow ];

        for( int i = 0; i < numOfChildren; i++ ){
            children[ ( i % numOfChildren ) / kidsPerRow ][ i / numOfChildren ][ i % kidsPerRow ] = i;
        }

        for( int i = 0; i < numOfChildren; i++ ){
            int max = ( i / kidsPerRow + 1 ) * kidsPerRow;
            int min = ( i / kidsPerRow ) * kidsPerRow;
            for( int j = min; j < max; j++  ){
                adjacencyTable[ i ][ j ] = adjacencyTable[ j ][ i ] = 1;
            }
        }        

        for( int day = 1; day < numOfDays; day++ ){
            for( int row = 0; row < kidsPerRow; row++ ){
                children[ row ][ day ][ 0 ] = row;
            }
        }


        int index = numOfChildren;
        int checkEntry;
        int currentEntry;
        int currentRow;
        int currentDay;
        int currentCol;
        
        unavailable = new boolean [ numOfDays ][ numOfChildren ];
        for( int day = 0; day < numOfDays; day++ ){
            for( int child = 0; child < kidsPerRow; child++ ){
                unavailable[ day ][ child ] = true;
            }
        }

        int maxIndex = kidsPerRow*kidsPerRow;

        while( children[ rows - 1 ][ numOfDays - 1 ][ kidsPerRow - 1 ] == 0 ){
            if( index % kidsPerRow == 0 && ( ( index % numOfChildren ) <= kidsPerRow*( kidsPerRow - 1 ) ) ){
                index++;
            }
            checkEntry = makeLegalEntry( index, rows , kidsPerRow, numOfChildren, children );

            currentCol = index % kidsPerRow;
            currentRow = ( index % numOfChildren ) / kidsPerRow;
            currentDay = index / numOfChildren;
            currentEntry = index % numOfChildren;

            if( checkEntry == -1 ){
                if( children[ currentRow ][ currentDay ][ currentCol] != 0 ){
                    fillTable( children , currentRow, currentDay, currentCol, false );
                    unavailable[ currentDay ][ children[ currentRow ][ currentDay ][ currentCol] ] = false;                
                }
                children[ currentRow ][ currentDay ][ currentCol ] = 0;
                if( ( currentCol == 1 ) && ( ( currentEntry ) < maxIndex ) ){
                    index -= 2;
                    continue;
                }
                index = ( currentEntry == 1 ) ? index - 2 : index - 1;
            }else{
                unavailable[ currentDay ][ checkEntry ] = true;
                children[ currentRow ][ currentDay ][ currentCol] = checkEntry;
                fillTable( children , currentRow, currentDay, currentCol, true );
                index++;
            }
        }
        printChildren( children, rows, numOfDays, kidsPerRow );

    }

    public static void fillTable( int [][][] kids, int currentRow, int currentDay, int lastIndex, boolean state ){
        int adjacentChild;
        int currentChild = kids[ currentRow ][ currentDay ][ lastIndex ];
        for( int i = lastIndex - 1; i >= 0; i-- ){
            adjacentChild = kids[ currentRow ][ currentDay ][ i ];             
            adjacencyTable[ currentChild ][ adjacentChild ] = adjacencyTable[ adjacentChild ][ currentChild ] = ( state ) ? 1 : 0;
        }
    }

    public static void printTable( int [][] table ){
        int rows = table.length;
        int length = table[ 0 ].length;
        for( int i = 0; i < rows; i++ ){
            for( int j = 0 ; j < length; j++ ){
                System.out.printf( "%-4d", table[ i ][ j ] );
            }
            System.out.print( "\n" );
        }
        System.out.print( "\n" );
    }

    public static void printChildren( int [][][] table, int rows, int numOfDays, int kidsPerRow ){
        for( int row = 0; row < rows; row++ ){
            for( int day = 0; day < numOfDays; day ++ ){
                for( int kid = 0; kid < kidsPerRow; kid++ ){
                    System.out.printf( "%-4d",table[ row ][ day ][ kid ] );
                }
                System.out.print( " | ");
            } 
            System.out.println("");
        }
        System.out.println("");
    }

    // This checks only the row if adding the new child is valid. The makeLegalEntry takes care of 
    // those that cannot be here for the overall table.
    public static boolean isValid( int child, int [] kidRow, int kidsPerRow, int index ){
        int ind = index % kidRow.length;
        for( int i = 0; i < ind ; i++ ){
            if( adjacencyTable[ child ][ kidRow[ i ] ] == 1 ){
                return false;
            }
        }
        return true;

    }

    public static int makeLegalEntry( int index, int numOfRows, int kidsPerRow, int numOfKids, int [][][] kids ){
        int candidate;
        int currentEntry = index % kidsPerRow;
        int currentDay =  index / numOfKids;
        int currentRow = ( index % numOfKids ) / kidsPerRow;
        int currentChild = kids[ currentRow ][ currentDay ][ currentEntry ];     
        boolean haveCandidate = false;

        if( currentChild!= 0 ){
            fillTable( kids , currentRow, currentDay, currentEntry, false );                
            unavailable[ currentDay ][ currentChild ] = false;
            candidate = currentChild + 1;
        }else{
            if( currentEntry == 0  ){
                candidate = kids[ currentRow - 1 ][ currentDay ][ currentEntry ] + 1; 
            }else{
                candidate = kids[ currentRow ][ currentDay ][ currentEntry - 1 ] + 1;
            }
        }

        if( ( currentEntry == 0 && candidate > currentRow * kidsPerRow ) || ( candidate  + ( kidsPerRow - currentEntry ) >= numOfKids ) ) {
            return -1;
        }

        while( !haveCandidate ){
            haveCandidate = false;
            for( int i = candidate; i < numOfKids; i++ ){
                if( !unavailable[ currentDay ][ i ] ){                    
                    candidate = i;
                    haveCandidate = true;
                    break;
                }
            }
            if( haveCandidate ){
                if( isValid( candidate, kids[ currentRow ][ currentDay ], kidsPerRow, index ) ){
                    return candidate;
                }else{
                    candidate++;
                    haveCandidate = false;
                    continue;
                }
            }else{
                return -1;
            }
        }
        return candidate;
    }

}