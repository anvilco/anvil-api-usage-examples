namespace AnvilExamples.examples;

public interface IBaseExample
{
    Task Run(string apiKey);
}

abstract class RunnableBaseExample : IBaseExample
{
    public virtual Task Run(string apiKey)
    {
        throw new NotImplementedException();
    }

    public virtual Task Run(string apiKey, string otherArg)
    {
        throw new NotImplementedException();
    }
}