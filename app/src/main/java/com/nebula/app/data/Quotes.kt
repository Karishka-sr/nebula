package com.nebula.app.data

import java.time.LocalDate

/**
 * Встроенная библиотека мотивационных цитат.
 * Каждая цитата — пара "текст — автор", на русском или английском.
 */
object Quotes {

    val all: List<Quote> = listOf(
        Quote("Всё, что ты можешь себе представить, — реально.", "Пабло Пикассо"),
        Quote("Победа начинается с желания попробовать ещё раз.", "Автор неизвестен"),
        Quote("Не бойся медленного пути, бойся стоять на месте.", "Китайская пословица"),
        Quote("Успех — это умение двигаться от неудачи к неудаче, не теряя энтузиазма.", "Уинстон Черчилль"),
        Quote("Секрет перемен: сосредоточь всю энергию не на борьбе со старым, а на создании нового.", "Сократ"),
        Quote("Пока ты дышишь, ты способен на большее.", "Автор неизвестен"),
        Quote("Лучшее время посадить дерево было двадцать лет назад. Второе лучшее время — сейчас.", "Китайская пословица"),
        Quote("Маленькие шаги каждый день приводят к большим результатам.", "Автор неизвестен"),
        Quote("Дисциплина — это мост между целями и их достижением.", "Джим Рон"),
        Quote("Ты — это то, что ты делаешь каждый день.", "Аристотель"),
        Quote("Начни там, где ты есть. Используй то, что у тебя есть. Делай то, что можешь.", "Артур Эш"),
        Quote("Сложности закаляют сильных и разрушают слабых.", "Автор неизвестен"),
        Quote("Одна маленькая победа сегодня — завтрашний фундамент.", "Автор неизвестен"),
        Quote("Фокус — это способность говорить «нет» хорошему ради великого.", "Джим Коллинз"),
        Quote("Делай то, что можешь, с тем, что имеешь, там, где ты есть.", "Теодор Рузвельт"),
        Quote("Вдохновение — это гость, который не любит посещать ленивых.", "Пётр Чайковский"),
        Quote("Тот, кто хочет — ищет возможности. Тот, кто не хочет — ищет причины.", "Авраам Линкольн"),
        Quote("Каждая минута планирования экономит десять минут работы.", "Алекс Маккензи"),
        Quote("Держи взгляд на звездах, а ноги на земле.", "Теодор Рузвельт"),
        Quote("Никогда не сдавайся: великие дела требуют времени.", "Автор неизвестен"),

        Quote("Believe you can and you're halfway there.", "Theodore Roosevelt"),
        Quote("The secret of getting ahead is getting started.", "Mark Twain"),
        Quote("It always seems impossible until it's done.", "Nelson Mandela"),
        Quote("Do the hard jobs first. The easy jobs will take care of themselves.", "Dale Carnegie"),
        Quote("Well done is better than well said.", "Benjamin Franklin"),
        Quote("Amateurs sit and wait for inspiration; the rest of us just get up and go to work.", "Stephen King"),
        Quote("Action is the foundational key to all success.", "Pablo Picasso"),
        Quote("You don't have to be great to start, but you have to start to be great.", "Zig Ziglar"),
        Quote("Success is not final, failure is not fatal: it is the courage to continue that counts.", "Winston Churchill"),
        Quote("Discipline is choosing between what you want now and what you want most.", "Abraham Lincoln"),
        Quote("Quality is not an act, it is a habit.", "Aristotle"),
        Quote("The future depends on what you do today.", "Mahatma Gandhi"),
        Quote("Either you run the day or the day runs you.", "Jim Rohn"),
        Quote("Little by little, one travels far.", "J.R.R. Tolkien"),
        Quote("Motivation gets you going, but habit keeps you growing.", "John C. Maxwell"),
        Quote("Don't count the days, make the days count.", "Muhammad Ali"),
        Quote("The best way to predict the future is to create it.", "Peter Drucker"),
        Quote("Energy and persistence conquer all things.", "Benjamin Franklin"),
        Quote("Start where you are. Use what you have. Do what you can.", "Arthur Ashe"),
        Quote("What you get by achieving your goals is not as important as what you become by achieving your goals.", "Zig Ziglar"),

        Quote("Действие — ключ к любому успеху.", "Пабло Пикассо"),
        Quote("Порядок освобождает мысль.", "Рене Декарт"),
        Quote("Кто не знает, куда идёт, рискует прийти не туда.", "Лоренс Питер"),
        Quote("Утром главное — начать.", "Автор неизвестен"),
        Quote("Терпение и труд всё перетрут.", "Русская пословица"),
        Quote("Вода камень точит.", "Русская пословица"),
        Quote("Семь раз отмерь, один раз отрежь.", "Русская пословица"),
        Quote("Делу время, потехе час.", "Русская пословица"),
        Quote("Без труда не вытащишь и рыбку из пруда.", "Русская пословица"),
        Quote("Тише едешь — дальше будешь.", "Русская пословица"),
        Quote("Хочешь изменить мир — начни с себя.", "Лев Толстой"),
        Quote("Сила не в крике, а в спокойствии.", "Автор неизвестен"),
        Quote("Каждый день — новая возможность изменить свою жизнь.", "Автор неизвестен"),
        Quote("Привычка формируется повторением.", "Аристотель"),
        Quote("Цель без плана — всего лишь желание.", "Антуан де Сент-Экзюпери"),
        Quote("Порядок в мыслях — порядок в делах.", "Автор неизвестен"),
        Quote("Самый тёмный час — перед рассветом.", "Английская пословица"),
        Quote("Сделай первый шаг — дорога появится сама.", "Автор неизвестен"),
        Quote("Побеждает не самый сильный, а самый упорный.", "Автор неизвестен"),
        Quote("Ты сильнее, чем думаешь.", "Автор неизвестен"),

        Quote("The journey of a thousand miles begins with a single step.", "Lao Tzu"),
        Quote("Success is the sum of small efforts, repeated day in and day out.", "Robert Collier"),
        Quote("Happiness is not something ready made. It comes from your own actions.", "Dalai Lama"),
        Quote("What we achieve inwardly will change outer reality.", "Plutarch"),
        Quote("He who has a why to live can bear almost any how.", "Friedrich Nietzsche"),
        Quote("A year from now you may wish you had started today.", "Karen Lamb"),
        Quote("If you want to make a permanent change, stop focusing on the size of your problems and start focusing on the size of you.", "T. Harv Eker"),
        Quote("Do what you can, with what you have, where you are.", "Theodore Roosevelt"),
        Quote("The meaning of life is to find your gift. The purpose of life is to give it away.", "Pablo Picasso"),
        Quote("In the middle of difficulty lies opportunity.", "Albert Einstein"),

        Quote("Чем больше делаешь, тем больше успеваешь.", "Автор неизвестен"),
        Quote("Сначала сформируй привычку, потом привычка сформирует тебя.", "Автор неизвестен"),
        Quote("Никто не становится мастером в один день.", "Автор неизвестен"),
        Quote("Тишина — тоже ответ.", "Автор неизвестен"),
        Quote("Завтра может быть слишком поздно.", "Автор неизвестен"),
        Quote("Капля пробивает камень не силой, а частотой падения.", "Овидий"),
        Quote("Не откладывай на завтра то, что можешь сделать сегодня.", "Бенджамин Франклин"),
        Quote("Дорогу осилит идущий.", "Сенека"),
        Quote("Лучше сделать и пожалеть, чем не сделать и жалеть вечно.", "Автор неизвестен"),
        Quote("Своя ноша не тянет.", "Русская пословица"),
        Quote("Умение ждать — первый признак мудрости.", "Автор неизвестен"),
        Quote("Мы — то, что мы повторяем. Совершенство — не действие, а привычка.", "Аристотель"),
        Quote("Успех приходит к тем, кто продолжает двигаться, даже когда трудно.", "Автор неизвестен"),
        Quote("Счастье — не пункт назначения, а способ путешествия.", "Маргарет Ли Ранбек"),
        Quote("Каждая неудача — шаг к успеху.", "Автор неизвестен"),
        Quote("Если хочешь — найдёшь время. Если не хочешь — найдёшь причину.", "Автор неизвестен"),
        Quote("Ты не знаешь, на что способен, пока не попробуешь.", "Автор неизвестен"),
        Quote("Глаза боятся, а руки делают.", "Русская пословица"),
        Quote("Делай сегодня то, что другие не хотят, — завтра будешь жить так, как другие не могут.", "Джерри Райс"),
        Quote("Один маленький шаг сегодня изменяет всё завтра.", "Автор неизвестен"),

        Quote("Don't watch the clock; do what it does. Keep going.", "Sam Levenson"),
        Quote("The only way to do great work is to love what you do.", "Steve Jobs"),
        Quote("Whether you think you can or you think you can't, you're right.", "Henry Ford"),
        Quote("A goal without a plan is just a wish.", "Antoine de Saint-Exupéry"),
        Quote("Success usually comes to those who are too busy to be looking for it.", "Henry David Thoreau"),
        Quote("Simplicity is the ultimate sophistication.", "Leonardo da Vinci"),
        Quote("It's not whether you get knocked down, it's whether you get up.", "Vince Lombardi"),
        Quote("The harder I work, the luckier I get.", "Gary Player"),

        Quote("Спокойствие — половина успеха.", "Автор неизвестен"),
        Quote("Никогда не поздно стать тем, кем ты мог бы быть.", "Джордж Элиот"),
        Quote("Мечты требуют упорства и времени.", "Автор неизвестен"),
        Quote("Всё приходит вовремя для тех, кто умеет ждать.", "Автор неизвестен"),
        Quote("Сегодня читатель — завтра лидер.", "Маргарет Фуллер"),
        Quote("Знание — сила.", "Фрэнсис Бэкон"),
        Quote("Кто ищет, тот всегда найдёт.", "Владимир Высоцкий"),
        Quote("Усердие всё превозмогает.", "Латинская пословица"),
        Quote("Смотри вперёд и не оглядывайся.", "Автор неизвестен"),
        Quote("Лучший способ предсказать будущее — создать его.", "Питер Друкер")
    )

    /**
     * Возвращает цитату дня — стабильно выбирается по дате,
     * чтобы в течение дня цитата не менялась.
     */
    fun quoteOfTheDay(date: LocalDate = LocalDate.now()): Quote {
        val dayOfYear = date.dayOfYear
        val seed = date.year * 31 + dayOfYear
        return all[(seed and Int.MAX_VALUE) % all.size]
    }
}

data class Quote(
    val text: String,
    val author: String
)