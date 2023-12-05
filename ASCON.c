/*ASCON implementation
Authors: SRIVASTAVA SHIVANG, WAN YONG AND HENG HIAN HEE
*/

//Include basic stdio.h
#include <stdio.h>
#include <time.h>
#include <sys/time.h>
#include <unistd.h>
//Ensure to use 64 bit values
typedef unsigned __int64 b64;
//Define the internal state
b64 internal_state[5]={0}, t[5]={0};
//Define constants to be added in permutation operations
b64 constants[12]={0xf0,0xe1,0xd2,0xc3,0xb4,0xa5,0x96,0x87,0x78,0x69,0x5a,0x4b};

//print the current state
b64 print_current_state(b64 internal_state[5])
{
    for(int i=0;i<5;i++)
    {
        //Show the 64 hexademical state. adding 016 ensures we also print 0s on the screen
        printf("%016I64x\n",internal_state[i]);
    }
}

//Now we work on the 3 permutation operations

//Add constants to row 2
void round_constant(b64 internal_state[5],int i,int x)
{
    //add to row 2 the constant depending on the current round and #rounds
    internal_state[2]=internal_state[2]^constants[12-x+i];

}
//S box implemtation
void s_box(b64 x[5])
{
//single pass applied to 64 different columns
//modification of 5 bit states taken from the official document directly
x[0] ^= x[4]; x[4] ^= x[3]; x[2] ^= x[1];
t[0] = x[0]; t[1] = x[1]; t[2] = x[2]; t[3] = x[3]; t[4] = x[4];
t[0] =~ t[0]; t[1] =~ t[1]; t[2] =~ t[2]; t[3] =~ t[3]; t[4] =~ t[4];
t[0] &= x[1]; t[1] &= x[2]; t[2] &= x[3]; t[3] &= x[4]; t[4] &= x[0];
x[0] ^= t[1]; x[1] ^= t[2]; x[2] ^= t[3]; x[3] ^= t[4]; x[4] ^= t[0];
x[1] ^= x[0]; x[0] ^= x[4]; x[3] ^= x[2]; x[2] =~ x[2];
}
//Rotation operation function for the linear diffusion layer (rotate a bits)
b64 rotate_bits( b64 x,int a)
{
    b64 rotated_a_bits;
    /*Provide correct rotate operation
    make sure that left most a bits correct, so we use left shift XORd
    */
    rotated_a_bits=(x>>a) ^ (x<<(64-a));
    return rotated_a_bits;
}
//This is the linear diffusion layer
void linear_diffusion(b64 state[5])
{   
    //Modification of row 1:  Σ0(x0) = x0 ⊕ (x0 ≫ 19) ⊕ (x0 ≫ 28)
    b64 t1,t2;
    t1=rotate_bits(internal_state[0],19);
    t2=rotate_bits(internal_state[0],28);
    internal_state[0]^=t1^t2;

    //Modification of row 2:Σ1(x1) = x1 ⊕ (x1 ≫ 61) ⊕ (x1 ≫ 39)
    b64 t3,t4;
    t3=rotate_bits(internal_state[1],61);
    t4=rotate_bits(internal_state[1],39);
    internal_state[1]^=t3^t4;

    //Modification of row 3 Σ2(x2) = x2 ⊕ (x2 ≫ 1) ⊕ (x2 ≫ 6)
    b64 t5,t6;
    t5=rotate_bits(internal_state[2],1);
    t6=rotate_bits(internal_state[2],6);
    internal_state[2]^=t5^t6;

    //Modification of row 4 Σ3(x3) = x3 ⊕ (x3 ≫ 10) ⊕ (x3 ≫ 17)
    b64 t7,t8;
    t7=rotate_bits(internal_state[3],10);
    t8=rotate_bits(internal_state[3],17);
    internal_state[3]^=t7^t8;

    //Modification of row 5 Σ4(x4) = x4 ⊕ (x4 ≫ 7) ⊕ (x4 ≫ 41)
    b64 t9,t10;
    t9=rotate_bits(internal_state[4],7);
    t10=rotate_bits(internal_state[4],41);
    internal_state[4]^=t9^t10;
}

/*
Permutation function operations run in a loop x times
x defines how many times the permutation opeartion 
is to be performed (12 or 6)
*/
void permutations(b64 internal_state[5], int x)
{
    for(int i=0;i<x;i++)
    {
        round_constant(internal_state,i,x);
        s_box(internal_state);
        linear_diffusion(internal_state);
    }
}
//Next we work on the initilation of internal state (initialization phase)
void initialization(b64 internal_state[5],b64 key[2])
{       
    //perform the first 12 rounds of permutation
    permutations(internal_state,12);
    //XOR the key with the last 128 bits of the initial state
    internal_state[3]^=key[0];
    internal_state[4]^=key[1];
}
//length is number of blocks needed to be encrypted t
void encryption(b64 internal_state[5],int length, b64 plain_text[],b64 cipher_text[])
{
    /*first ciphertext is the first row of initialized state XOR'd 
    with the first plaintext block*/
    cipher_text[0]=plain_text[0]^internal_state[0];
    internal_state[0]=cipher_text[0];
    for (int i=1;i<length;i++)
    {
        //perform 6 rounds of permutation from the previous state
        permutations(internal_state,6);
        //get ith block of ciphertext
        cipher_text[i]=plain_text[i]^internal_state[0];
        //modify state to be fed to each 6 round permutations
        internal_state[0]=cipher_text[i];
    }
}
void decryption(b64 internal_state[5],int length,b64 cipher_text[],b64 plain_text_decrypted[])
{
    //first ciphertext is the first row of initialized state XOR'd with the first plaintext block
    plain_text_decrypted[0]=cipher_text[0]^internal_state[0];
    internal_state[0]=cipher_text[0];
    for (int i=1;i<length;i++)
    {
        //perform 6 rounds of permutation from the previous state
        permutations(internal_state,6);
        //get ith block of ciphertext
        plain_text_decrypted[i]=cipher_text[i]^internal_state[0];
        //modify state to be fed to each 6 round permutations
        internal_state[0]=cipher_text[i];
    }
}
//perform finalization phase to get the key 
void finalization_phase(b64 internal_state[5], b64 key[2])
{
    //XOR the first 2 rows of c (2nd and 3rd row of the state) with the key
    internal_state[1]^=key[0];
    internal_state[2]^=key[1];
    permutations(internal_state,12);
    //at the end, the last 2 rows of the state are XOR'd with the key to produce the tag
    internal_state[3]^=key[0];
    internal_state[4]^=key[1];
}
//Main loop
void main() {
    // Write C code here
    //kllprintf("Hello world\n");
    //define nonce, key and IV
    b64 IV=0x80400c0600000000;
    b64 key[2]={0xece2cafb8397c3c7,0x075b889de2e32b69};
    b64 nonce[2]={0xe85bd7b5eca7924e,0x1d2691e5bf4c40c3};
    b64 plain_text[]={0x1234567890abcdef,0xabcdef1234567890,0xabcdef9876543210};
    b64 cipher_text[3]={0};
    //setting up initial internal state
    internal_state[0]=IV;
    internal_state[1]=key[0];
    internal_state[2]=key[1];
    internal_state[3]=nonce[0];
    internal_state[4]=nonce[1];
    initialization(internal_state,key);
    printf("\n\nEncryption: \n");
    printf("Encryption initialized state: \n");
    print_current_state(internal_state);
    encryption(internal_state, 3, plain_text,cipher_text);
    printf("Ciphertext: %016I64x %016I64x %016I64x\n",cipher_text[0],cipher_text[1],cipher_text[2]);
    printf("State after plaintext (encryption) phase \n");
    print_current_state(internal_state);
    finalization_phase(internal_state,key);
    printf("Generated Tag: %016I64x %016I64x\n",internal_state[3],internal_state[4]);
    printf("Encryption Final state: \n");
    print_current_state(internal_state);
    
    //decryption
    //Parameters provided by encryptor

    b64 plain_text_decrypted[3]={0};
    internal_state[0]=IV;
    internal_state[1]=key[0];
    internal_state[2]=key[1];
    internal_state[3]=nonce[0];
    internal_state[4]=nonce[1];
    initialization(internal_state,key);
    printf("\nDecryption: \n");
    printf("Decryption inititalized state \n");
    print_current_state(internal_state);
    decryption(internal_state, 3,cipher_text, plain_text_decrypted);
    printf("Decrypted plaintext: %016I64x %016I64x %016I64x\n",plain_text_decrypted[0],plain_text_decrypted[1],plain_text_decrypted[2]);
    printf("State after decryption phase \n");
    print_current_state(internal_state);
    finalization_phase(internal_state,key);
    printf("Re computed Tag: %016I64x %016I64x\n",internal_state[3],internal_state[4]);
    printf("Decryption final state: \n");
    print_current_state(internal_state);
}



/*
//Use this version of main for calculating average speed of execution for authenticated encryption process over 100 times
//Main loop
void main() {
    

    double total_time = 0.0;
     for (int i = 0; i < 100; i++)
    {
        clock_t start_time = clock();
        // Perform the operation to be timed here
        b64 IV=0x80400c0600000000;
        b64 key[2]={0xece2cafb8397c3c7,0x075b889de2e32b69};
        b64 nonce[2]={0xe85bd7b5eca7924e,0x1d2691e5bf4c40c3};
        b64 plain_text[]={0x1234567890abcdef,0xabcdef1234567890,0xabcdef9876543210};
        b64 cipher_text[3]={0};
        //setting up initial internal state
        internal_state[0]=IV;
        internal_state[1]=key[0];
        internal_state[2]=key[1];
        internal_state[3]=nonce[0];
        internal_state[4]=nonce[1];
        initialization(internal_state,key);
        encryption(internal_state, 3, plain_text,cipher_text);
        finalization_phase(internal_state,key);
        usleep(2000); // Sleep for 2 milliseconds
        clock_t end_time = clock();
        // Calculate overall duration in milliseconds and output it (subtract the 2 ms sleep)
        double duration = ((double)(end_time - start_time) / (CLOCKS_PER_SEC / 1000))-2 ;
        total_time += duration;
     
     }
     double average_time = total_time / 100;
    printf("Average time of authenticated encryption process (ASCON 128) %f milliseconds\n", average_time);
}
*/