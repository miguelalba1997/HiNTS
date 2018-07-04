def main():
  
     R1 = int(input("enter resistor R1"))
     R2 = int(input("enter resistor R2"))
     R1 = int(input("enter resistor R3"))
     R1 = int(input("enter resistor R4"))
     R1 = int(input("enter resistor R5"))
     R1 = int(input("enter resistor R6"))
     R1 = int(input("enter resistor R7"))
     R1 = int(input("enter resistor R8"))

     A = R1 + R2 + R3
     B = R2 + R6 + R7
     C = R4 + R5 - R3
     D = R8 + R4 + R7

     VIN = 5
    
     I1 = ((-(AC - R3*R3)*(BD - R7*R7)*(R3))/((R4*R2*R3)*(R7 - B)*A)) + (-VIN*R4*R7*R2*(A+R3))/((A*C-R3*R3)*(B*D-R7*R7)) - VIN/A
     I2 = (-(AC - R3*R3)*(BD - R7*R7))/((R4*R2*R3)*(R7 - B))
     I3 = ((VIN*R4*R7)*(A+R3))/((AC - R3*R3)*(BD - R7*R7))
     I4 = ((-VIN*R4*B)*(A+R3))/((AC - R3*R3)*(BD - R7*R7))

     REQ = (I1 + I2 + I3 + I4)/VIN

     print REQ
    
