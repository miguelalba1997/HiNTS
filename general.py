## Miguel Alba ----> Zimanyi Group 
## 23 August 2018
##
##
##
## This program sets up the equations to 
## solve for the equivalent resistance of 
## a resistor network, utilizing the mesh 
## method and gauss jordan elimination.

import random
import numpy as np

def system_solver(matrix, cells, num_cols):
## This function uses gauss jordan elimination to solve the linear 
## system of kirkchoff loop equations.
    
    ## This creates the final augmented matrix
    ## and calculates its determinant to make
    ## sure it is invertable.
    aug_matrix = np.array(matrix)
    np.linalg.det(aug_matrix)

    print("augmented matrix:\n", aug_matrix, "\n", "determinant = ", np.linalg.det(aug_matrix), "\n")

    ## This is the total voltage that each loop 
    ## should be equal to. 
    vin = [0]*(cells)
    for i in range(0, num_cols - 1):
        vin[i] = -5 
    print("B(v in vector):", "\n", vin)

    ## Solve Ax = b by making x = inv(A)b.
    current = np.dot((np.linalg.inv(aug_matrix)),vin)

    print("current vector:")
    for i in range(cells):
        print(current[i])

def make_equation(row, col, num_rows, num_col):
## This creates the list of equations to be solved by gaussian
## elimination.
   
    cells = (num_col - 1) * (num_rows - 1)
    equation = []
    matrix = []

    ## Here resistors insert into a cell counter clockwise
    ## this is done for consistency.

    for i in range(cells):
        if (i <= num_col - 2):
            equation.append(col[i])
            equation.append(0)# append zero for the "zero" value resistor on the edges/corners
            equation.append(col[i+1])
            equation.append(row[i]) 
            matrix.append(equation)
            equation = []
        elif(i > cells - num_col):
            equation.append(col[i+1])
            equation.append(row[i - num_col + 1])
            equation.append(col[i+2])
            equation.append(0)# append zero for the "zero" value resistor on the edges/corners
            matrix.append(equation)
            equation = []
        else:
            equation.append(col[i+1])
            equation.append(row[i - num_col + 1])
            equation.append(col[i+2])
            equation.append(row[i])
            matrix.append(equation)
            equation = []

    print("initial cell  matrix:", "\n", np.matrix(matrix)) 
   
    equation = [0] * cells 
    
    for i in range(cells):
    ## Each matrix row entry will have four entries since its
    ## a square grid that we are building. Hence no j indexing for
    ## the second item in the matrix. 

        # these next 4 if statements take care of the conditions for the corners of this grid.
        if i == 0:
            equation[i] = ( matrix[i][0] + matrix[i][1] + matrix[i][2] + matrix[i][3])
            equation[i + 1] = (-1 * matrix[i][2])
            equation[i + num_col-1] = (-1 * matrix[i][3])
            matrix[i] = equation
            equation = [0]*cells
        elif i == num_col - 2:
            equation[i] = ( matrix[i][0] + matrix[i][1] + matrix[i][2] + matrix[i][3])
            equation[i - 1] = (-1 * matrix[i][0])
            equation[i + num_col - 1] = (-1 * matrix[i][3])
            matrix[i] = equation
            equation = [0]*cells
        elif i == cells - num_col + 1:
            equation[i] = ( matrix[i][0] + matrix[i][1] + matrix[i][2] + matrix[i][3])
            equation[i + 1] = (-1 * matrix[i][2])
            equation[i - (num_col - 1)] = (-1 * matrix[i][1])
            matrix[i] = equation
            equation = [0]*cells
        elif i == cells - 1:
            equation[i] = ( matrix[i][0] + matrix[i][1] + matrix[i][2] + matrix[i][3])
            equation[i - 1] = (-1 * matrix[i][0])
            equation[i - (num_col - 1)] = (-1 * matrix[i][1])
            matrix[i] = equation
            equation = [0]*cells
        # these next two if statements take care of the top and bottom rows. 
        elif i > 0 and i < num_col -2:
            equation[i] = ( matrix[i][0] + matrix[i][1] + matrix[i][2] + matrix[i][3])
            equation[i + 1] = (-1 * matrix[i][0])
            equation[i - 1] = (-1 * matrix[i][2])
            equation[i + num_col-1] = (-1 * matrix[i][3])
            matrix[i] = equation
            equation = [0]*cells
        elif i > cells - num_col + 1 and i < cells - 1:
            equation[i] = ( matrix[i][0] + matrix[i][1] + matrix[i][2] + matrix[i][3])
            equation[i + 1] = (-1 * matrix[i][0])
            equation[i - 1] = (-1 * matrix[i][2])
            equation[i - (num_col-1)] = (-1 * matrix[i][1])
            matrix[i] = equation
            equation = [0]*cells
        # these next if statents take care of the sides
        elif i % (num_col - 1) == 0 and i != cells - num_col + 1:
            equation[i] = ( matrix[i][0] + matrix[i][1] + matrix[i][2] + matrix[i][3])
            equation[i + 1] = (-1 * matrix[i][2])
            equation[i + num_col-1] = (-1 * matrix[i][3])
            equation[i - (num_col-1)] = (-1 * matrix[i][1])
            matrix[i] = equation
            equation = [0]*cells
        elif (i+1) % (num_col - 1) == 0 and i != cells -1:
            equation[i] = ( matrix[i][0] + matrix[i][1] + matrix[i][2] + matrix[i][3])
            equation[i - 1] = (-1 * matrix[i][0])
            equation[i + num_col-1] = (-1 * matrix[i][3])
            equation[i - (num_col-1)] = (-1 * matrix[i][1])
            matrix[i] = equation
            equation = [0]*cells
        # This final statement takes care of the center cells in the grid
        else:
            equation[i] = ( matrix[i][0] + matrix[i][1] + matrix[i][2] + matrix[i][3])
            equation[i - 1] = (-1 * matrix[i][0])
            equation[i + 1] = (-1 * matrix[i][2])
            equation[i + num_col-1] = (-1 * matrix[i][3])
            equation[i - (num_col-1)] = (-1 * matrix[i][1])
            matrix[i] = equation
            equation = [0]*cells

    print("resistor matrix", "\n", np.matrix(matrix)) 

    system_solver(matrix, cells, num_col)

def create_system(num_rows, num_col):
    ## This creates the system for a grid of resistors
    ## it only works for a square grid. Note Num_col and Num_rows
    ## correspondes to rows and columns of resistors NOT rows and 
    ## columns of Cells. so a 3 x 3 grid would have 4 total cells.
   
    row = []
    col = []
    resistorlist = []    
    total = (num_col*(num_rows-1)) + ((num_rows-2)*(num_col-1))
    skip = (2 * num_col) - 1 
    
    ## This creates a random list of resistors to put in the other lists.
    ## Eventually this will be replaced by a simple file read in for values.
    for k in range(0, total):
        x = random.randrange(1, 20, 1)
        resistorlist.insert(k, x)
    length = len(resistorlist)  
    
    for i in range(0, length, skip):
        if(i + num_col <= length):
            for j in range(i, i + num_col):
                 col.append(resistorlist[j])
        else:
            break
        if( j + num_col < length ):
            for k in range(j+1, j + num_col):
                row.append(resistorlist[k])
        else:
            break
    make_equation(row, col, num_rows, num_col)

def main():

    num_rows = int(input("how many rows would you like?"))
    num_col = int(input("how many columns would you like?"))   
    print("\n")
    
    create_system(num_rows, num_col)

main()
