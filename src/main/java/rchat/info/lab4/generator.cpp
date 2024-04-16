#include <codecvt>
#include <iostream>
#include <map>
#include <random>
#include <string>
#include <locale>
#include <fstream>

std::string HartliGenerator(int n) {
    std::string t;
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_int_distribution<unsigned char> d(0, 127);
    for (int i = 0; i < n; i++) {
        t.push_back(d(gen));
    }
    return t;
}
std::string BernoulliGenerator(int n, float pivot) {
    std::string t;
    std::random_device rd;
    std::mt19937 gen(rd());
    std::bernoulli_distribution d(pivot);
    for (int i = 0; i < n; i++) {
        unsigned char res;
        for (int j = 0; j < 8; j++) {
            res += res * 2 + d(gen);
        }
        t.push_back(res);
    }
    return t;
}

int main() {
    std::ofstream out("out.txt");

    out << BernoulliGenerator(100, 0.5);

    out.flush();
    out.close();
}