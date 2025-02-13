package com.swiss;

import com.swiss.entity.Employee;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {



    private static Set<Employee> belowAverageManagers = new HashSet<>();
    private static Set<Employee> aboveAverageManagers = new HashSet<>();

    private static Set<Employee> onBarManagers = new HashSet<>();

    private static Set<Employee> longHeirarchy = new HashSet<>();
    public static void main(String[] args) {
        String filePath = "src/main/resources/employees.csv";
        List<Employee> employees = readCSV(filePath);

        Map<Integer, Employee> employeeMap = new HashMap<>();

        for(Employee employee : employees){
            employeeMap.put(employee.getId(), employee);
        }



        Map<Integer, EmployeeReporteeMapper> reporteeMap = new HashMap<>();

        for (Employee employee : employees){
            if(employee.getManagerId() != null)
            {
                if(reporteeMap.containsKey(employee.getManagerId())){

                    EmployeeReporteeMapper employeeReporteeMapper = reporteeMap.get(employee.getManagerId());
                    employeeReporteeMapper.setTotalReportees(employeeReporteeMapper.getTotalReportees() + 1);
                    employeeReporteeMapper.setCommulativeSalaries(employeeReporteeMapper.getCommulativeSalaries() + employee.getSalary());

                    reporteeMap.put(employee.getManagerId(), employeeReporteeMapper);

                }
                else{
                    EmployeeReporteeMapper reporteeMapper = new EmployeeReporteeMapper();
                    reporteeMapper.setCommulativeSalaries(employee.getSalary());
                    reporteeMap.put(employee.getManagerId(), reporteeMapper);
                }
            }
        }

        //lets calculate earn less / earn more scenarios first
        for(Employee employee : employees){
            if(employee.getManagerId() != 0){

                EmployeeReporteeMapper reporteeMapper = reporteeMap.get(employee.getManagerId());
                double averageSalary = reporteeMapper.getCommulativeSalaries() / reporteeMapper.getTotalReportees();
                double managerSalary = employeeMap.get(employee.getManagerId()).getSalary();

                if((averageSalary+ (averageSalary*0.2) )> managerSalary){
                    belowAverageManagers.add(employeeMap.get(employee.getManagerId()));


                }
                else if(((averageSalary*0.5)+ averageSalary) < managerSalary){

                    aboveAverageManagers.add(employeeMap.get(employee.getManagerId()));

                }
                else{
                    onBarManagers.add(employeeMap.get(employee.getManagerId()));
                }

            }
        }


        for(Employee employee : employees){

            int managerid = employee.getManagerId();

            if(managerid == 0){
                System.out.println("CEO found "+ employee.getFirstName() + " "+ employee.getLastName() );
            }

            int level = 0;
            while(managerid != 0 && employeeMap.containsKey(managerid)){
                level++;
                managerid = employeeMap.get(managerid).getManagerId();

            }

            if(level > 4){
                longHeirarchy.add(employee);
            }
        }

        System.out.println("Managers with less than 20 percent of average salary of their reportees--> "+ belowAverageManagers);

        System.out.println("Managers with more than 50 percent of average salary of their reportees--> "+ aboveAverageManagers);

        System.out.println("Managers with perfect salary bar--> "+ onBarManagers);

        System.out.println("Reportees with long hirarchy--> "+ longHeirarchy);



    }


    public static List<Employee> readCSV(String filePath) {
        List<Employee> employees = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                int id = Integer.parseInt(values[0].trim());
                String firstName = values[1].trim();
                String lastName = values[2].trim();
                int salary = Integer.parseInt(values[3].trim());
                Integer managerId = values.length > 4 && !values[4].trim().isEmpty() ? Integer.parseInt(values[4].trim()) : 0;
                employees.add(new Employee(id, firstName, lastName, salary, managerId));
            }
        } catch (IOException e) {
           //
        }
        return employees;
    }

    static class EmployeeReporteeMapper{
        private double commulativeSalaries;

        private int totalReportees = 1;



        public double getCommulativeSalaries() {
            return commulativeSalaries;
        }

        public void setCommulativeSalaries(double commulativeSalaries) {
            this.commulativeSalaries = commulativeSalaries;
        }

        public int getTotalReportees() {
            return totalReportees;
        }

        public void setTotalReportees(int totalReportees) {
            this.totalReportees = totalReportees;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EmployeeReporteeMapper that = (EmployeeReporteeMapper) o;
            return Double.compare(that.commulativeSalaries, commulativeSalaries) == 0 && totalReportees == that.totalReportees;
        }

        @Override
        public int hashCode() {
            return Objects.hash(commulativeSalaries, totalReportees);
        }
    }

}