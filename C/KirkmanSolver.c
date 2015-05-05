#include <stdio.h>
#include <stdlib.h>

int isValidInput(int, int);
void fillFirstDay(int rows, int columns, int [][rows][columns]);
void fillFirstRows(int rows, int columns, int, int [][rows][columns]);
void printTable(int rows, int columns, int, int [][rows][columns]);

int main(int argc, char **argv) {

     int numOfRows, numOfColumns, numOfKids, numOfDays;

     if (argc != 3) {
         printf("Invalid number of arguments. Please enter number of rows and number of columns.\n");
         return 1;
     }

     char * end;
     numOfRows = (int)strtol(argv[1], &end, 10);
     numOfColumns = (int)strtol(argv[2], &end , 10);
  
     if(!isValidInput(numOfRows, numOfColumns)) {
         printf("Impossible!\n");
         return 1;
     }
    
     numOfKids = numOfRows * numOfColumns;
     /*
      * Each child needs to share a row with every other child 
      * in the group. Each day that child can share a row with
      * at most numOfColumns - 1 students. The total number of 
      * days required to share a row with all the other 
      * numOfKids - 1 students is the ratio given below.
      */
     numOfDays = (numOfKids - 1)/(numOfColumns - 1);
     int solution[numOfDays][numOfRows][numOfColumns];
     fillFirstDay(numOfRows, numOfColumns, solution);
     fillFirstRows(numOfRows, numOfColumns, numOfDays, solution);
     printTable(numOfRows, numOfColumns, numOfDays, solution);
     printf("We have %i rows and %i columns.\n",numOfRows, numOfColumns);
     
     return 0;
};

/*
 * This problem can't be solved if the number of days
 * required to share a row with every other child once
 * and only once is not an integer.
 */
int isValidInput(int rows, int cols ) {
    return (( rows * cols - 1 ) % ( cols - 1 ) == 0);
}

/*
 * The first day is simple. We list the children in ascending 
 * order, left-to-right, top-to-bottom.
 */
void fillFirstDay(int rows, int cols, int solution[][rows][cols]) { 
    int child, numOfKids;
    for(child = 0, numOfKids = rows * cols; child < numOfKids; child += 1) {
        solution[0][child / cols ][child % cols] = child;
    }
}

/*
 * Since we know that kids 0, 1, ... cols - 1 have already
 * shared a row, we can designate them to be the first kids in 
 * rows 0 through cols - 1. 
 */
void fillFirstRows(int rows, int cols, int numOfDays, int solution[][rows][cols]) {
    int child, day, row;
    for(day = 1; day < numOfDays; day += 1) {
        for(row = 0; row < cols; row += 1) {
            solution[day][row][0] = row;
        }
    }
}

void printTable(int rows, int cols, int numOfDays, int solution[][rows][cols]) {
    int row, col, day;
    for(row = 0; row < rows; row += 1) {
        for(day = 0; day < numOfDays; day += 1) {
            for(col = 0; col < cols; col += 1) {
                 printf("%-3i", solution[day][row][col]);
            }
            printf("| ");
        }
        printf("\n");
    }
}
