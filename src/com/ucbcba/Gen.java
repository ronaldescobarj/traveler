package com.ucbcba;

import java.util.*;
import java.util.stream.Collectors;

public class Gen {

    private static final float crossProbability = (float) 0.7;
    private static final float mutationProbability = (float) 0.001;
    private static final int populationSize = 1000;
    private static final int numberOfCities = 10;
    private static final int chromosomeSize = numberOfCities + 1;
    private static final float convergencePercentage = (float) 1.0;
    private static final int[][] distances = {{0, 5, 9, 1, 8, 5, 1, 4, 4, 2},
            {5, 0, 8, 6, 7, 4, 2, 6, 5, 3},
            {9, 8, 0, 4, 2, 6, 3, 5, 2, 1},
            {1, 6, 4, 0, 3, 5, 5, 3, 3, 4},
            {8, 7, 2, 3, 0, 4, 2, 2, 4, 2},
            {5, 4, 6, 5, 4, 0, 5, 3, 2, 3},
            {1, 2, 3, 5, 2, 5, 0, 1, 4, 4},
            {4, 6, 5, 3, 2, 3, 1, 0, 3, 5},
            {4, 5, 2, 3, 4, 2, 4, 3, 0, 3},
            {2, 3, 1, 4, 2, 3, 4, 5, 3, 0}};

    public int getPositionOfChar(char character) {
        return character - 65;
    }

    public int getDistance(char a, char b) {
        return distances[getPositionOfChar(a)][getPositionOfChar(b)];
    }

    public int fitness(String state) {
        int fit = 0;
        for (int i = 0; i < state.length() - 1; i++) {
            fit += getDistance(state.charAt(i), state.charAt(i + 1));
        }
        return 60 - fit;
    }

    public String generateRandomString() {
        char city = 'B';
        List<Character> cities = new ArrayList<>();
        char[] charArray = new char[chromosomeSize];
        for (int i = 0; i < numberOfCities - 1; i++) {
            cities.add(city);
            city++;
        }
        Collections.shuffle(cities);
        cities.add(0, 'A');
        cities.add('A');
        for (int i = 0; i < chromosomeSize; i++)
            charArray[i] = cities.get(i);
        return new String(charArray);
    }

    public List<Pair<String, Integer>> generatePopulation() {
        List<Pair<String, Integer>> population = new ArrayList<>();
        String indivudual;
        while (population.size() != populationSize) {
            indivudual = generateRandomString();
            population.add(new Pair<>(indivudual, fitness(indivudual)));
        }
        return population;
    }

    public boolean doesItConverge(List<Pair<String, Integer>> population) {
        Map<Integer, Integer> fitnessValues = new TreeMap<>();
        //List<Pair<String, Integer>> fitnessValues = new ArrayList<>();
        int elementsThatMustBeEqual = (int) (population.size() * convergencePercentage);

        for (Pair<String, Integer> keyValue : population) {
            if (fitnessValues.containsKey(keyValue.getValue()))
                fitnessValues.put(keyValue.getValue(), fitnessValues.get(keyValue.getValue()) + 1);
            else
                fitnessValues.put(keyValue.getValue(), 1);
        }

        for (Integer value : fitnessValues.values()) {
            if (value >= elementsThatMustBeEqual)
                return true;
        }
        return false;
    }

    public List<String> skewedRoulette(List<Pair<String, Integer>> population) {
        List<String> list = new ArrayList<>(Collections.emptyList());
        for (Pair<String, Integer> entry : population) {
            for (int i = 0; i < entry.getValue(); i++)
                list.add(entry.getKey());
        }
        return list;
    }

    public List<Character> removeInitialCity(List<Character> state) {
        state.remove(0);
        state.remove(state.size() - 1);
        return state;
    }

    public String crossingFirstSon(String father, String mother) {
        List<Character> fatherAsAList = father.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
        List<Character> motherAsAList = mother.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
        char[] firstSon = new char[chromosomeSize];
        int firstSonIndex, isPair = 0;
        char firstCity = father.charAt(0);
        fatherAsAList = removeInitialCity(fatherAsAList);
        motherAsAList = removeInitialCity(motherAsAList);

        firstSon[0] = firstCity;
        firstSon[chromosomeSize - 1] = firstCity;
        firstSon[1] = fatherAsAList.get(0);
        fatherAsAList.remove(0);

        if (firstSon[1] == motherAsAList.get(1)) {
            firstSon[3] = motherAsAList.get(2);
            motherAsAList.remove(2);
            fatherAsAList.remove(new Character(firstSon[3]));

            firstSon[2] = fatherAsAList.get(0);
            fatherAsAList.remove(0);
            motherAsAList.remove(new Character(firstSon[2]));
            firstSonIndex = 4;
        } else {
            firstSon[2] = motherAsAList.get(1);
            motherAsAList.remove(1);
            fatherAsAList.remove(new Character(firstSon[2]));
            firstSonIndex = 3;
        }
        motherAsAList.remove(new Character(firstSon[1]));

        while (firstSonIndex < chromosomeSize - 1) {
            if (isPair % 2 == 0) {
                firstSon[firstSonIndex] = fatherAsAList.get(0);
                fatherAsAList.remove(0);
                motherAsAList.remove(new Character(firstSon[firstSonIndex]));
            } else {
                firstSon[firstSonIndex] = motherAsAList.get(0);
                motherAsAList.remove(0);
                fatherAsAList.remove(new Character(firstSon[firstSonIndex]));
            }
            firstSonIndex++;
            isPair++;
        }
        return new String(firstSon);
    }

    public String crossingFirstSon2(String father, String mother) {
        Random random = new Random();
        int l1, l2, inferiorLimit, superiorLimit;

        List<Character> fatherAsAList = father.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
        List<Character> motherAsAList = mother.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
        char initialCity = fatherAsAList.get(0);
        fatherAsAList = removeInitialCity(fatherAsAList);
        motherAsAList = removeInitialCity(motherAsAList);

        do {
            l1 = random.nextInt(fatherAsAList.size());
            l2 = random.nextInt(fatherAsAList.size());
        }
        while (l2 == l1 || l1 == 0 && l2 == (fatherAsAList.size() - 1) || l1 == (fatherAsAList.size() - 1) && l2 == 0);
        inferiorLimit = l1 < l2 ? l1 : l2;
        superiorLimit = l1 > l2 ? l1 : l2;
        char[] firstSon = new char[chromosomeSize];
        firstSon[0] = initialCity;
        firstSon[chromosomeSize - 1] = initialCity;
        for (int i = inferiorLimit; i <= superiorLimit; i++) {
            firstSon[i + 1] = fatherAsAList.get(i);
            motherAsAList.remove(new Character(firstSon[i + 1]));
        }
        for (int index = 0; index < chromosomeSize; index++) {
            if (firstSon[index] == Character.MIN_VALUE) {
                firstSon[index] = motherAsAList.remove(0);
            }
        }
        return new String(firstSon);
    }

    public String crossingSecondSon2(String father, String mother) {
        Random random = new Random();
        int l1, l2, inferiorLimit, superiorLimit;

        List<Character> fatherAsAList = father.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
        List<Character> motherAsAList = mother.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
        char initialCity = fatherAsAList.get(0);
        fatherAsAList = removeInitialCity(fatherAsAList);
        motherAsAList = removeInitialCity(motherAsAList);

        do {
            l1 = random.nextInt(fatherAsAList.size());
            l2 = random.nextInt(fatherAsAList.size());
        }
        while (l2 == l1 || l1 == 0 && l2 == (fatherAsAList.size() - 1) || l1 == (fatherAsAList.size() - 1) && l2 == 0);
        inferiorLimit = l1 < l2 ? l1 : l2;
        superiorLimit = l1 > l2 ? l1 : l2;
        char[] firstSon = new char[chromosomeSize];
        firstSon[0] = initialCity;
        firstSon[chromosomeSize - 1] = initialCity;
        for (int i = inferiorLimit; i <= superiorLimit; i++) {
            firstSon[i + 1] = motherAsAList.get(i);
            fatherAsAList.remove(new Character(firstSon[i + 1]));
        }
        for (int index = 0; index < chromosomeSize; index++) {
            if (firstSon[index] == Character.MIN_VALUE) {
                firstSon[index] = fatherAsAList.remove(0);
            }
        }
        return new String(firstSon);
    }


    public String crossingSecondSon(String father, String mother) {
        List<Character> fatherAsAList = father.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
        List<Character> motherAsAList = mother.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
        char[] secondSon = new char[chromosomeSize];
        int secondSonLimit, isPair = 0;
        char firstCity = father.charAt(0);
        fatherAsAList = removeInitialCity(fatherAsAList);
        motherAsAList = removeInitialCity(motherAsAList);

        secondSon[0] = firstCity;
        secondSon[chromosomeSize - 1] = firstCity;

        secondSon[chromosomeSize - 2] = fatherAsAList.get(fatherAsAList.size() - 1);
        fatherAsAList.remove(fatherAsAList.size() - 1);

        if (secondSon[chromosomeSize - 2] == motherAsAList.get(motherAsAList.size() - 2)) {
            secondSon[chromosomeSize - 4] = motherAsAList.get(motherAsAList.size() - 3);
            motherAsAList.remove(motherAsAList.size() - 3);
            fatherAsAList.remove(new Character(secondSon[chromosomeSize - 4]));

            secondSon[chromosomeSize - 3] = fatherAsAList.get(fatherAsAList.size() - 1);
            fatherAsAList.remove(fatherAsAList.size() - 1);
            motherAsAList.remove(new Character(secondSon[chromosomeSize - 3]));
            secondSonLimit = chromosomeSize - 4;
        } else {
            secondSon[chromosomeSize - 3] = motherAsAList.get(motherAsAList.size() - 2);
            motherAsAList.remove(motherAsAList.size() - 2);
            fatherAsAList.remove(new Character(secondSon[chromosomeSize - 3]));
            secondSonLimit = chromosomeSize - 3;
        }
        motherAsAList.remove(new Character(secondSon[chromosomeSize - 2]));

        for (int i = 1; i < secondSonLimit; i++) {
            if (isPair % 2 == 0) {
                secondSon[i] = fatherAsAList.get(0);
                fatherAsAList.remove(0);
                motherAsAList.remove(new Character(secondSon[i]));
            } else {
                secondSon[i] = motherAsAList.get(0);
                motherAsAList.remove(0);
                fatherAsAList.remove(new Character(secondSon[i]));
            }
            isPair++;
        }
        return new String(secondSon);
    }

    public List<String> crossing(String father, String mother) {
        List<String> descendents = new ArrayList<>(Collections.emptyList());
        descendents.add(crossingFirstSon2(father, mother));
        descendents.add(crossingSecondSon2(father, mother));
        return descendents;
    }

    public String mutate(String state) {
        Random random = new Random();
        int probability = 1;
        float aux = mutationProbability;
        while (aux < 1) {
            probability *= 10;
            aux *= 10.0;
        }
        probability = (probability / ((int) aux));
        int randomNumber = random.nextInt(probability);
        char[] charArray = state.toCharArray();
        String mutated = state;
        //numero arbitrario para compararlo, ya que la probabilidad es 1 en 1000
        if (randomNumber == 10) {
            // System.out.println("MUTATED!");
            int positionToMutate = random.nextInt(chromosomeSize - 3) + 1;
            char temp = charArray[positionToMutate];
            charArray[positionToMutate] = charArray[positionToMutate + 1];
            charArray[positionToMutate + 1] = temp;
            mutated = new String(charArray);
        }
        return mutated;
    }

    public List<Pair<String, Integer>> reducePopulation(List<Pair<String, Integer>> population) {
        List<Pair<String, Integer>> reducedPopulation = new ArrayList<>();

        // Se toma el menor fitness como el mejor
        population.sort((o1, o2) -> (o2.getValue().compareTo(o1.getValue())));

        for (int i = 0; i < populationSize; i++)
            reducedPopulation.add(population.get(i));

        return reducedPopulation;
    }

    public String simpleGeneticAlgorithm() {
        List<Pair<String, Integer>> population = generatePopulation();
        List<Pair<String, Integer>> temporaryPopulation = new ArrayList<>();
        List<String> skewedRoulette;
        Random random = new Random();
        String I1, I2, D1, D2;
        int skewedRouletteLength, generations = 0;
        boolean converge = false;
        while (!converge) {
            //calcular el numero de cruces de acuerdo a la probabilidad
            // tam poblacion (multiplicado) prob de cruce
            temporaryPopulation.clear();
            int n = (int) (populationSize * crossProbability) / 2;
            while (n != 0) {
                // seleccionar los dos individuos que cruzaran entre ellos(sexo)
                skewedRoulette = skewedRoulette(population);
                skewedRouletteLength = skewedRoulette.size();
                I1 = skewedRoulette.get(random.nextInt(skewedRouletteLength));
                I2 = skewedRoulette.get(random.nextInt(skewedRouletteLength));

                //cruzar a los individuos
                List<String> decendents = crossing(I1, I2);
                D1 = decendents.get(0);
                D2 = decendents.get(1);

                //mutar a los descendientes
                D1 = mutate(D1);
                D2 = mutate(D2);

                //insertar a los descendientes en una poblacion temporal
                temporaryPopulation.add(new Pair<>(D1, fitness(D1)));
                temporaryPopulation.add(new Pair<>(D2, fitness(D2)));

                //reducir el contador del while
                n--;
            }
            //union de popolation y temporaryPopulation
            population.addAll(temporaryPopulation);
            population = reducePopulation(population);
            generations++;
            if (doesItConverge(population))
                converge = true;
            //else
            //population = reducePopulation(population);
        }
        System.out.println("Generaciones necesarias: " + generations);
        // for (Pair<String, Integer> p : population)
        //  System.out.print(p.getKey() + "=" + (-p.getValue() + 60) + ", ");
        // System.out.println();
        System.out.println(population.get(0).getKey() + "=" + (-population.get(0).getValue() + 60));
        //retornar el mejor cromosoma
        return Collections.max(population, Comparator.comparing(Pair::getValue)).getKey();
    }
}
