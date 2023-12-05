
# -*- coding: utf-8 -*-
"""
Created on Tue Oct 31 01:05:45 2023

@author: shivang
"""
#import libraries
import numpy as np
import math
import random 

#factorization function
def rsa(N):
    #choose a random value starting point between 0 to 50..
    rand_start=random.randint(0,50)
    x=rand_start
    y=rand_start
    foundP=False
    
    #Run loop till it returns
    while foundP==False:
        #Use rho polland to do f^2(x) and f(y)
        for _ in range(2):
            x = (x ** 2 + 1) % N
        y = (y ** 2 + 1) % N
        
        #calculate GCD of abs(y-x) and N
        gcd=math.gcd(abs(x-y),N)
        #it is important to note that this is directly used to find collision location 
        #by reverse engineering to see if non trivial GCD found
        #O(N^1/4*logN)
        
        #if x=y, restart the process with a different random 
        if(gcd==N):
            rand_start=random.randint(0,50)
            x=rand_start
            y=rand_start
        
        #else if gcd>1,return gcd(abs(x-y),N) 
        elif gcd>1:
            foundP=True
            #note that the above line may not be required and is optional
            return gcd
        
        #if gcd ==1 loop continues continue (to continue rho polland), implying this is not a collision location yet

#Call the function for C1-3
N=669238341814177962683573
p=(rsa(N))
#q=n/p (other factor)
q=int(N/p)
#print the values
print(f"N: {N}")
print(f"P: {p}")
print(f"Q: {q}")