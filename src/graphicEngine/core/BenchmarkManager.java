package graphicEngine.core;

public class BenchmarkManager {
    public enum State {
        IDLE,
        WARMUP,
        MEASURING,
        COOL_DOWN,
        FINISHED
    }

    private State currentState = State.IDLE;
    private final GraphicEngineContext graphicEngineContext;

    private int startFrameOfCurrentState = 0;
    private long startTime = 0;
    private long endTime = 0;
    private final int WARMUP_FRAMES = 50;
    private final int MEASURE_FRAMES = 150;
    private final int COOLDOWN_FRAMES = 25;

    private double benchmarkDuration = 0;
    private double benchmarkAverageFPS = 0;

    public BenchmarkManager(GraphicEngineContext graphicEngineContext) {
        this.graphicEngineContext = graphicEngineContext;
    }

    public void start() {
        if (currentState == State.WARMUP || currentState == State.MEASURING) return;

        System.out.println("--- BENCHMARK STARTED ---");
        System.out.println("--- WARM UP ---");
        this.currentState = State.WARMUP;

        this.startFrameOfCurrentState = graphicEngineContext.getElapsedFrame();

        this.benchmarkAverageFPS = 0;
        this.benchmarkDuration = 0;
    }

    public void update() {
        if (currentState == State.IDLE || currentState == State.FINISHED) return;

        int currentFrame = graphicEngineContext.getElapsedFrame();
        int framesInState = currentFrame - startFrameOfCurrentState;

        switch (currentState) {
            case WARMUP:
                if (framesInState >= WARMUP_FRAMES) {
                    System.out.println("--- WARMUP DONE ---");
                    System.out.println("--- START OF MEASURE ---");
                    this.currentState = State.MEASURING;
                    this.startTime = System.nanoTime();

                    this.startFrameOfCurrentState = currentFrame;
                }
                break;

            case MEASURING:
                if (framesInState >= MEASURE_FRAMES) {
                    System.out.println("--- MEASURE DONE ---");
                    System.out.println("--- START OF COOL DOWN ---");
                    endTime = System.nanoTime();
                    this.currentState = State.COOL_DOWN;

                    this.startFrameOfCurrentState = currentFrame;
                }
                break;

            case COOL_DOWN:
                if (framesInState >= COOLDOWN_FRAMES) {
                    System.out.println("--- COOL DOWN DONE ---");
                    finish();
                }
                break;
            default:
                break;
        }
    }

    public void finish() {
        System.out.println("--- END OF BENCHMARK ---");

        this.benchmarkDuration = (endTime - startTime)/1_000_000_000.0;
        this.benchmarkAverageFPS = MEASURE_FRAMES / benchmarkDuration;

        this.currentState = State.FINISHED;

        System.out.println("=== BENCHMARK RESULT ===");
        System.out.println("Avg FPS  : " + String.format("%.2f", benchmarkAverageFPS));
        System.out.println("Duration : " + String.format("%.2f", benchmarkDuration) + "s");
    }

    public double getBenchmarkDuration() {
        return benchmarkDuration;
    }

    public double getBenchmarkAverageFPS() {
        return benchmarkAverageFPS;
    }
}
