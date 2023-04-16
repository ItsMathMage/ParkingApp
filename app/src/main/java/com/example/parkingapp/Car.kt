package com.example.parkingapp

class Car (
    var firm: String,
    var model: String,
    var color: String,
    var number: String
) {
    constructor() : this("", "", "", "")

    // Конструктор з усіма властивостями класу
    init {
        // Ініціалізуємо властивості за допомогою переданих параметрів
        this.firm = firm
        this.model = model
        this.color = color
        this.number = number
    }
}