/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logicgates;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import javafx.util.Pair;
/**
 *
 * @author Not a gaming laptop
 */
public class LogicGates {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        circuit c=new circuit();
        Pair left=new Pair <>(true,true);
        Pair right=new Pair <>(true,true);
        Gate g=new Gate("and",left,right,1,0,"front");
        c.makeRoot(g);

        left=new Pair <>(true,false);
        right=new Pair <>(true,false);
        g=new Gate("and",left,right,2,1,"back");
        c.addGate(g);

        left=new Pair <>(true,false);
        right=new Pair <>(true,false);
        g=new Gate("and",left,right,3,1,"back");

        c.addGate(g);

        c.finalBool(c.root);
    }
}
/*
    HOW TO ADD:
    EACH GATE HAS CONNECTION TYPE:
                                    FRONT: WILL BE ADDED IN THE BACK OF ANOTHER NODE, SO IT'S UP WILL BE CONNECTED

                                    BACK:  WILL BE ADDED IN THE FRONT OF ANOTHER GATE, SO ITS LEFT OR RIGHT WILL BE CONNECTED


                                    PAIRS : FIRST BOOLEAN IS TRUE THEN IT CAN BE CONNECTED TO SOMETHING
                                            SECOND BOOLEAN IS THE ACTUAL VALUE IF ITS NOT CONNECTED

*/
class Gate{
    String name;
    Pair <Boolean,Boolean>left;
    Pair <Boolean,Boolean>right;
    int ID;
    int connectedTo;//ID
    String connectionType;
    Gate(String name,Pair left,Pair right,int ID,int connectedTo,String connectionType){
        this.name=name;
        this.left=left;
        this.right=right;
        this.ID=ID;
        this.connectionType=connectionType;
        this.connectedTo=connectedTo;
    }
}
class circuitNode{
    boolean value;
    boolean hasValue;
    circuitNode up,left,right;
    Gate gate;
    circuitNode(Gate gate){
        this.gate=gate;
        left=null;
        up=null;
        right=null;
        hasValue=false;
    }
}
class circuit{
    circuitNode root;
    public void makeRoot(Gate gate){
        root =new circuitNode(gate);
    }
    public void addGate(Gate gate){
        circuitNode temp=root;
        circuitNode newNode=new circuitNode(gate);
        while(true){
            if(newNode.gate.connectionType.equals("back")){ //if this node is connected from behind

                if(newNode.gate.connectedTo==temp.gate.ID){
                    temp.up=newNode;
                    if(newNode.gate.left.getKey()){//if true then it can be connected
                        newNode.left=temp;
                        System.out.println("added gate : "+newNode.gate.ID+" to gate : "+temp.gate.ID);
                        break;
                    }else{
                        newNode.right=temp;
                        System.out.println("added gate : "+newNode.gate.ID+" to gate : "+temp.gate.ID);
                        break;
                    }
                }
            }
            else if(newNode.gate.connectionType.equals("front")){
                while(temp.up!=null)
                    temp=temp.up;

                Stack<circuitNode> savePoint=new Stack<>();
                ArrayList <Integer> visited=new ArrayList();
                savePoint.add(temp);
                while(!savePoint.isEmpty()){
                    temp=savePoint.peek();
                    if(temp.gate.ID==newNode.gate.connectedTo){
                        newNode.up=temp;
                        if(temp.left==null){
                            temp.left=newNode;
                            System.out.println("added gate : "+newNode.gate.ID+" to gate : "+temp.gate.ID);
                            break;
                        }else{
                            temp.right=newNode;
                            System.out.println("added gate : "+newNode.gate.ID+" to gate : "+temp.gate.ID);
                            break;
                        }
                    }else if(temp.left!=null&&!visited.contains(temp.left.gate.ID)){
                        temp=temp.left;
                        savePoint.add(temp);
                        break;
                    }else if(temp.right!=null&&!visited.contains(temp.right.gate.ID)){
                        temp=temp.right;
                        savePoint.add(temp);
                        break;
                    }else{
                        visited.add(temp.gate.ID);
                        temp=savePoint.pop();
                        break;
                    }
                }

            }
            temp=temp.up;
        }
        root=temp;
    }
    public void calculate(circuitNode root){
        circuitNode temp=root;
        while(temp.up!=null)
            temp=temp.up;
        Stack<circuitNode> savePoint=new Stack<>();
        savePoint.push(temp);
        while(!savePoint.isEmpty()){
            temp=savePoint.peek();
            if(temp.left!=null&&!temp.left.hasValue&&temp.right==null){
                System.out.println(temp.gate.ID);
                temp=temp.left;
                savePoint.push(temp);
            }else if(temp.left==null&&temp.right==null){
                System.out.println(temp.gate.ID);
                temp.hasValue=true;
                if(!temp.gate.name.equals("not"))
                    temp.value=calculate(temp.gate.name,temp.gate.left.getValue(),temp.gate.right.getValue());
                else{
                    if(temp.left==null)
                        temp.value=!temp.right.value;
                    else
                        temp.value=!temp.left.value;
                }
                temp=savePoint.pop();
            }else if(temp.left!=null&&temp.left.hasValue&&temp.right==null){
                System.out.println(temp.gate.ID);
                temp.hasValue=true;
                if(!temp.gate.equals("not"))
                    temp.value=calculate(temp.gate.name,temp.left.value,temp.gate.right.getValue());
                else{
                    if(temp.left==null)
                        temp.value=!temp.right.value;
                    else
                        temp.value=!temp.left.value;
                }
                temp=savePoint.pop();
            }else if(temp.left!=null&&temp.right!=null){
                if(temp.left.hasValue&&temp.right.hasValue){
                    System.out.println(temp.gate.ID);
                    temp.hasValue=true;
                    if(temp.gate.equals("not"))
                        temp.value=calculate(temp.gate.name,temp.left.value,temp.right.value);
                    else{
                        if(temp.left==null)
                            temp.value=!temp.right.value;
                        else
                            temp.value=!temp.left.value;
                    }
                    temp=savePoint.pop();
                }else if(temp.right.hasValue){
                    System.out.println(temp.gate.ID);
                    temp=temp.left;
                    savePoint.push(temp);
                }else if(temp.left.hasValue){
                    System.out.println(temp.gate.ID);
                    temp=temp.right;
                    savePoint.push(temp);
                }else{
                    System.out.println(temp.gate.ID);
                    temp=temp.left;
                    savePoint.push(temp);
                }
            }
        }
    }
    public boolean calculate(String name,boolean input1,boolean input2){
        if(name.equals("and")){
            if(input1==false&&input2==false){
                return false;
            }else{
                return input1&&input2;
            }
        }else if(name.equals("or")){
            return input1||input2;
        }else if(name.equals("xor")){
            return !(input1&&input2);
        }else if(name.equals("xnor")){
            return input1&&input2;
        }else{//nand
            if(input1==true&&input2==true){
                return false;
            }else{
                return true;
            }
        }
    }
    public void finalBool(circuitNode root){
        calculate(root);
        circuitNode temp=root;
        while(temp.up!=null)
            temp=temp.up;
        System.out.println(temp.gate.ID);
        System.out.println(temp.value);
    }
}
