import java.util.Random;
import java.util.Scanner;

public class Homework2 {

public static void main(String[] args) {

// Setting minimum to 0 and creating max
int min=0,max;
Scanner sc = new Scanner(System.in);
System.out.print("Enter the value of P (only prime number)");
int p = Integer.parseInt(sc.next()); //scan whatever user puts in
max = p;
Random r = new Random();
  

int g = r.nextInt(max-min) + min;
System.out.println("The value of g selected: "+g);
int a =r.nextInt(max-min) + min;
int b = r.nextInt(max-min) + min;
  
System.out.println("The value of A selected by Alice: "+a);
System.out.println("the value of B selected by Bob: "+b);
  
  
int result1 = findKey(g,a,p);
int result2 = findKey(g,b,p);
  
  
System.out.println("The value of A sent to Bob by Alice: "+result1);
System.out.println("The value of A sent to Alice by Bob: "+result2);
  
int key1 = findKey(result2,a,p);
int key2 = findKey(result1,b,p);
  
System.out.println("Key1 and Key2 generated are same "+(key1 == key2));
System.out.println("The value of key shared between Alice and Bob "+key1);
  
}

static int findKey(int a,int b,int mod)
{
int t;
if(b==1)
return a;
t=findKey(a,b/2,mod);
if(b%2==0)
return (t*t)%mod;
else
return (((t*t)%mod)*a)%mod;
}
}