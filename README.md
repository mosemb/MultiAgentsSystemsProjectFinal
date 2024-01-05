To run the StudentChoice use the Supervisor.java or ThesisCommt.java file as the configurations file. The following arguments would surfice
in the text parameters field. 
-gui "student:masProject.Student(StudentChoice);
ThesisCommittee:masProject.ThesisCommt(James,Ricky,Brice,Jimmy);
supervisor:masProject.Supervisor(Supervised_Learning,Software_Requirement_Engineering, A,B,C,D,E);
reviewer:masProject.Reviewer;thesis:masProject.Thesis"

To run the Company choice use Student.java file 
-gui  "student:masProject.Student(Company);ThesisCommittee:masProject.ThesisCommt(James,Ricky,Brice,Jimmy);
supervisor:masProject.Supervisor(Supervised_Learning,Software_Requirement_Engineering, A,B,C,D,E);reviewer:masProject.Reviewer;
thesis:masProject.Thesis" 

To run the Proposals choice use Supervisor.java file
-gui  "student:masProject.Student(Proposals);ThesisCommittee:masProject.ThesisCommt(James,Ricky,Brice,Jimmy);
supervisor:masProject.Supervisor(Supervised_Learning,Software_Requirement_Engineering, A,B,C,D,E);reviewer:masProject.Reviewer;
thesis:masProject.Thesis" 

To run the YellowPages choice use the following 
-gui  "student:masProject.Student(YellowPages);ThesisCommittee:masProject.ThesisCommt(James,Ricky,Brice,Jimmy);
supervisor:masProject.Supervisor(Supervised_Learning,Software_Requirement_Engineering, A,B,C,D,E);reviewer:masProject.Reviewer;
thesis:masProject.Thesis" 

To run the ThesisCommittee choice use the following 
-gui "student:masProject.Student(ThesisComm);ThesisCommittee:masProject.ThesisCommt(James,Ricky,Brice,Jimmy);
ThesisCommittee1:masProject.ThesisCommt(James,Ricky,Brice,Jimmy);ThesisCommittee2:masProject.ThesisCommt(James,Ricky,Brice,Jimmy);
ThesisCommittee3:masProject.ThesisCommt(James,Ricky,Brice,Jimmy);ThesisCommittee4:masProject.ThesisCommt(James,Ricky,Brice,Jimmy);
supervisor:masProject.Supervisor(Supervised_Learning,Software_Requirement_Engineering,A,B,C,D,E);reviewer:masProject.Reviewer;
thesis:masProject.Thesis" 

To run the Supervisor choice use the following
-gui
 "student:masProject.Student(Supervisor);ThesisCommittee:masProject.ThesisCommt(James,Ricky,Brice,Jimmy);
 supervisor:masProject.Supervisor(Supervised_Learning,Software_Requirement_Engineering,A,B,C,D,E); 
 supervisor1:masProject.Supervisor(Supervised_Learning,Software_Requirement_Engineering, A,B,C,D,E);
 supervisor2:masProject.Supervisor(Supervised_Learning,Software_Requirement_Engineering,A,B,C,D,E);
 supervisor3:masProject.Supervisor(Supervised_Learning,Software_Requirement_Engineering,A,B,C,D,E);
 supervisor4:masProject.Supervisor(Supervised_Learning,Software_Requirement_Engineering, A,B,C,D,E);
 reviewer:masProject.Reviewer;thesis:masProject.Thesis"
 
To run the Review choice use the following
-gui
 "student:masProject.Student(Reviewer);ThesisCommittee:masProject.ThesisCommt(James,Ricky,Brice,Jimmy);
 supervisor:masProject.Supervisor(Supervised_Learning,Software_Requirement_Engineering,A,B,C,D,E); 
 reviewer:masProject.Reviewer; reviewer1:masProject.Reviewer;reviewer2:masProject.Reviewer;
 reviewer3:masProject.Reviewer;reviewer4:masProject.Reviewer;thesis:masProject.Thesis"
